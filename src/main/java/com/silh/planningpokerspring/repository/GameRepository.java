package com.silh.planningpokerspring.repository;

import com.silh.planningpokerspring.Game;
import com.silh.planningpokerspring.Player;

import java.util.Optional;

public interface GameRepository {

  /**
   * Create a new game, assign ID to it and store it.
   *
   * @param creator - creator of the new game.
   * @return - game with id and creator assigned to it.
   */
  Game create(Player creator);

  /**
   * Get existing game by ID.
   *
   * @param id - if of the game.
   * @return - optional which contains found game, if it was found, or empty.
   */
  Optional<Game> find(String id);

  /**
   * Update game. ID of the game to update is taken from {@param updatedGame}
   *
   * @param updatedGame - updated game.
   * @return - optional which contains updated game, if it was found and updated, or empty.
   */
  Optional<Game> update(Game updatedGame);

  /**
   * Delete exiting game by ID.
   *
   * @param id - id of the game to delete.
   */
  void delete(String id);
}
