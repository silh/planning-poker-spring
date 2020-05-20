package com.silh.planningpokerspring.service;

import com.silh.planningpokerspring.Player;
import com.silh.planningpokerspring.RoundState;
import com.silh.planningpokerspring.converter.GameConverter;
import com.silh.planningpokerspring.dto.GameDto;
import com.silh.planningpokerspring.repository.GameRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GenericGameService implements GameService {

  private final GameRepository repository;
  private final GameConverter gameConverter;

  public GenericGameService(GameRepository repository,
                            GameConverter gameConverter) {
    this.repository = repository;
    this.gameConverter = gameConverter;
  }

  @Override
  public GameDto createGame(Player creator) {
    return gameConverter.convert(repository.create(creator));
  }

  @Override
  public Optional<GameDto> getGame(String gameId) {
    return repository.find(gameId)
      .map(gameConverter::convert);
  }

  public boolean joinGame(String gameId, Player player) {
    return repository.find(gameId)
      .map(game -> game.addParticipant(player))
      .orElse(false);
  }

  @Override
  public boolean transitionTo(String gameId, String personId, RoundState nextState) {
    return repository
      .findByIdAndOwnerId(gameId, personId)
      .map(game -> {
        game.transitionTo(nextState);
        return true;
      }).orElse(false);
  }

  @Override
  public boolean vote(String gameId, String voterId, Long value) {
    final Boolean updated = repository
      .find(gameId)
      .map(game -> game.addVote(voterId, value))
      .orElse(false);
    if (updated) {
      //TODO do notifications
    }
    return updated;
  }
}
