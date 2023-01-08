package com.silh.planningpokerspring.service;

import com.silh.planningpokerspring.converter.GameConverter;
import com.silh.planningpokerspring.domain.GameState;
import com.silh.planningpokerspring.domain.Player;
import com.silh.planningpokerspring.repository.GameRepository;
import com.silh.planningpokerspring.request.GameDto;

import java.util.List;
import java.util.Optional;

/**
 * This class is responsible for all things related to managing running games of planign poker.
 */
public class GenericGameService implements GameService {

  private final GameRepository repository;
  private final GameConverter gameConverter;
  private final List<GameEventsSubscriber> subscribers;

  public GenericGameService(GameRepository repository,
                            GameConverter gameConverter,
                            List<GameEventsSubscriber> subscribers) {
    this.repository = repository;
    this.gameConverter = gameConverter;
    this.subscribers = subscribers;
  }

  @Override
  public GameDto createGame(Player creator) {
    return gameConverter.convert(repository.create(creator));
  }

  @Override
  public List<GameDto> getGames() {
    return repository.findAll()
      .stream()
      .sorted()
      .map(gameConverter::convert)
      .toList();
  }

  @Override
  public Optional<GameDto> getGame(String gameId) {
    return repository.find(gameId)
      .map(gameConverter::convert);
  }

  @Override
  //TODO the logic here expects that the game will be updated somehow in repo which depends on current implementation
  //of the repository and that is not correct.
  public boolean joinGame(String gameId, Player player) {
    final Boolean updated = repository.find(gameId)
      .map(game -> game.addParticipant(player))
      .orElse(false);
    notifySubscribers(updated, gameId);
    return updated;
  }

  @Override
  public boolean transitionTo(String gameId, String personId, GameState nextState) {
    final Boolean updated = repository
      .findByIdAndOwnerId(gameId, personId)
      .map(game -> {
        game.transitionTo(nextState);
        return true;
      }).orElse(false);
    notifySubscribers(updated, gameId);
    return updated;
  }

  @Override
  public boolean vote(String gameId, String voterId, Long value) {
    final Boolean updated = repository
      .find(gameId)
      .filter(game -> game.getParticipants().containsKey(voterId))
      .map(game -> game.addVote(voterId, value))
      .orElse(false);
    notifySubscribers(updated, gameId);
    return updated;
  }

  private void notifySubscribers(Boolean updated, String gameId) {
    if (updated) {
      repository.find(gameId)
        .map(gameConverter::convert)
        .ifPresent(gameDto -> subscribers.forEach(subscriber -> subscriber.notify(gameDto)));
    }
  }
}
