package com.silh.planningpokerspring.service;

import com.silh.planningpokerspring.converter.GameConverter;
import com.silh.planningpokerspring.domain.GameState;
import com.silh.planningpokerspring.exception.UserNotFoundException;
import com.silh.planningpokerspring.repository.GameRepository;
import com.silh.planningpokerspring.repository.UserRepository;
import com.silh.planningpokerspring.request.GameDto;
import com.silh.planningpokerspring.request.NewGameRequest;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * This class is responsible for all things related to managing running games of planing poker.
 * It uses locks as it should be good enough for now.
 */
public class GenericGameService implements GameService {

  private final GameRepository gameRepository;
  private final UserRepository userRepository;
  private final GameConverter gameConverter;

  public GenericGameService(GameRepository gameRepository,
                            UserRepository userRepository,
                            GameConverter gameConverter) {
    this.gameRepository = gameRepository;
    this.userRepository = userRepository;
    this.gameConverter = gameConverter;
  }

  @Override
  public GameDto createGame(NewGameRequest req) {
    // throw an exception for now, better handled by Result
    final var creator = userRepository.find(req.creatorId())
      .orElseThrow(() -> new UserNotFoundException(String.format("user %s not found", req.creatorId())));
    return gameConverter.convert(gameRepository.create(req.gameName(), creator));
  }

  @Override
  public List<GameDto> getGames() {
    //noinspection DataFlowIssue
    return gameRepository.findAll()
      .stream()
      .map(gameConverter::convert) // Can't really return null here.
      .sorted(Comparator.comparing(GameDto::name))
      .toList();
  }

  @Override
  public Optional<GameDto> getGame(String gameId) {
    return gameRepository.find(gameId)
      .map(gameConverter::convert);
  }

  @Override
  public void joinGame(String gameId, String playerId) {
    userRepository.find(playerId)
      .ifPresent(player -> {
        gameRepository.find(gameId)
          .ifPresent(game -> game.addParticipant(player));
      });
  }

  @Override
  public void leaveGame(String gameId, String playerId) {
    gameRepository.find(gameId)
      .ifPresent(game -> game.removeParticipant(playerId));
  }

  @Override
  public void transitionTo(String gameId, String personId, GameState nextState) {
    gameRepository
//      .findByIdAndOwnerId(gameId, personId)// TODO should only owner be able to transition?
      .find(gameId)
      .ifPresent(game -> game.transitionTo(nextState));
  }

  @Override
  public void vote(String gameId, String voterId, String value) {
    gameRepository
      .find(gameId)
      .filter(game -> game.getPlayers().containsKey(voterId))
      .ifPresent(game -> game.addVote(voterId, value));
  }

}
