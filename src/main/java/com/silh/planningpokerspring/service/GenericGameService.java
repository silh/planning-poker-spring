package com.silh.planningpokerspring.service;

import com.silh.planningpokerspring.converter.GameConverter;
import com.silh.planningpokerspring.domain.GameState;
import com.silh.planningpokerspring.repository.GameRepository;
import com.silh.planningpokerspring.repository.UserRepository;
import com.silh.planningpokerspring.request.GameDto;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

/**
 * This class is responsible for all things related to managing running games of planing poker.
 * It uses locks as it should be good enough for now.
 */
public class GenericGameService implements GameService {

  private final GameRepository gameRepository;
  private final UserRepository userRepository;
  private final GameConverter gameConverter;
  private final List<GameEventsSubscriber> subscribers;

  private final ReadWriteLock lock;

  public GenericGameService(GameRepository gameRepository,
                            UserRepository userRepository,
                            GameConverter gameConverter,
                            List<GameEventsSubscriber> subscribers) {
    this.gameRepository = gameRepository;
    this.userRepository = userRepository;
    this.gameConverter = gameConverter;
    this.subscribers = subscribers;
    this.lock = new ReentrantReadWriteLock();
  }

  @Override
  public GameDto createGame(String creatorId) {
    // throw an exception for now, better handled by Result
    final var creator = userRepository.find(creatorId)
      .orElseThrow(() -> new RuntimeException(String.format("user %s not found", creatorId)));
    return doWriteLocked(() -> gameConverter.convert(gameRepository.create(creator)));
  }

  @Override
  public List<GameDto> getGames() {
    //noinspection DataFlowIssue
    return doReadLocked(() ->
      gameRepository.findAll()
        .stream()
        .map(gameConverter::convert) // Can't really return null here.
        .sorted(Comparator.comparing(GameDto::id))// TODO we should order by name but our games don't have names yet.
        .toList()
    );
  }

  @Override
  public Optional<GameDto> getGame(String gameId) {
    return doReadLocked(() ->
      gameRepository.find(gameId)
        .map(gameConverter::convert)
    );
  }

  @Override
  public boolean joinGame(String gameId, String playerId) {
    return doWriteLocked(() -> {
      // FIXME throw for now, handle with result later
      final var player = userRepository.find(playerId)
        .orElseThrow(() -> new RuntimeException(String.format("Player %s not found", playerId)));
      final boolean updated = gameRepository.find(gameId)
        .map(game -> game.addParticipant(player))
        .orElse(false);
      notifySubscribers(updated, gameId);
      return updated;
    });
  }

  @Override
  public boolean transitionTo(String gameId, String personId, GameState nextState) {
    return doWriteLocked(() -> {
      final boolean updated = gameRepository
        .findByIdAndOwnerId(gameId, personId)
        .map(game -> {
          game.transitionTo(nextState);
          return true;
        }).orElse(false);
      notifySubscribers(updated, gameId);
      return updated;
    });
  }

  @Override
  public boolean vote(String gameId, String voterId, Long value) {
    return doWriteLocked(() -> {
      final boolean updated = gameRepository
        .find(gameId)
        .filter(game -> game.getParticipants().containsKey(voterId))
        .map(game -> game.addVote(voterId, value))
        .orElse(false);
      notifySubscribers(updated, gameId);
      return updated;
    });
  }

  private void notifySubscribers(boolean updated, String gameId) {
    if (updated) {
      gameRepository.find(gameId)
        .map(gameConverter::convert)
        .ifPresent(gameDto ->
          subscribers.forEach(subscriber -> subscriber.notify(gameDto))
        );
    }
  }

  private void doReadLocked(Runnable action) {
    lock.readLock().lock();
    try {
      action.run();
    } finally {
      lock.readLock().unlock();
    }
  }

  private <T> T doReadLocked(Supplier<T> action) {
    lock.readLock().lock();
    try {
      return action.get();
    } finally {
      lock.readLock().unlock();
    }
  }

  private void doWriteLocked(Runnable action) {
    lock.writeLock().lock();
    try {
      action.run();
    } finally {
      lock.writeLock().unlock();
    }
  }

  private <T> T doWriteLocked(Supplier<T> action) {
    lock.writeLock().lock();
    try {
      return action.get();
    } finally {
      lock.writeLock().unlock();
    }
  }
}
