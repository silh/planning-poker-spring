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

  /**
   * Add player to a running game.
   *
   * @param gameId - id of the game to which user should be added.
   * @param player - player that should be added.
   * @return - true if player was added, false otherwise.
   */
  boolean joinGame(String gameId, Player player);

  /**
   * Move game to a new stage.
   *
   * @param gameId    - ID of the game that should be moved.
   * @param personId  - who tries to move the game.
   * @param nextState - state to which game should be moved.
   * @return - true if game was moved to a new state, false otherwise.
   */
  boolean transitionTo(String gameId, String personId, GameState nextState);

  /**
   * Added players vote.
   *
   * @param gameId  - ID of the game in which user tries to cast a vote.
   * @param voterId - ID of the voter.
   * @param value   - value of the cast vote.
   * @return - true if vote was added, false otherwise.
   */
  boolean vote(String gameId, String voterId, Long value);

  /**
   * Get current game state.
   *
   * @param gameId - ID of the game.
   * @return - Optional of game if the game with such ID exists, empty Optional otherwise.
   */
  Optional<GameDto> getGame(String gameId);
}
