package com.silh.planningpokerspring.service;

import com.silh.planningpokerspring.Game;
import com.silh.planningpokerspring.Player;
import com.silh.planningpokerspring.Round;
import com.silh.planningpokerspring.repository.GameRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GenericGameService implements GameService {

  private final GameRepository repository;

  public GenericGameService(GameRepository repository) {
    this.repository = repository;
  }

  @Override
  public Game createGame(Player creator) {
    return repository.create(creator);
  }

  @Override
  public boolean startRound(String gameId, String personId) {
    return doTransition(gameId, personId, Round::start);
  }

  @Override
  public boolean startDiscussion(String gameId, String personId) {
    return doTransition(gameId, personId, Round::discuss);
  }

  @Override
  public boolean restartVoting(String gameId, String personId) {
    return doTransition(gameId, personId, Round::returnToVoting);
  }

  @Override
  public boolean finishRound(String gameId, String personId) {
    return doTransition(gameId, personId, Round::finish);
  }

  @Override
  public boolean vote(String gameId, String voterId, Long value) {
    final Boolean updated = repository
      .find(gameId)
      .map(game -> game.getRound().vote(voterId, value))
      .orElse(false);
    if (updated) {
      //TODO do notifications
    }
    return updated;
  }

  @Override
  public Optional<Game> getGame(String gameId) {
    return repository.find(gameId);
  }

  private boolean doTransition(String gameId, String creatorId, RoundTransitionAction action) {
    final Boolean updated = repository
      .findByIdAndOwnerId(gameId, creatorId)
      .map(Game::getRound)
      .map(action)
      .orElse(false);
    if (updated) {
      //TODO do notifications
    }
    return updated;
  }
}
