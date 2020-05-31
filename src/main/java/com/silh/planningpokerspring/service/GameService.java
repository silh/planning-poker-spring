package com.silh.planningpokerspring.service;

import com.silh.planningpokerspring.domain.GameState;
import com.silh.planningpokerspring.domain.Player;
import com.silh.planningpokerspring.request.GameDto;

import java.util.Optional;

public interface GameService {
  /**
   * Create a new game.
   *
   * @param creator - creator of the new game.
   * @return - created Game.
   */
  GameDto createGame(Player creator);

  boolean joinGame(String gameId, Player player);

  boolean transitionTo(String gameId, String personId, GameState nextState);

  boolean vote(String gameId, String voterId, Long value);

  Optional<GameDto> getGame(String gameId);
}
