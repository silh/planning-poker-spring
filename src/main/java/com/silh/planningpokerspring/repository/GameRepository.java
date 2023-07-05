package com.silh.planningpokerspring.repository;

import com.silh.planningpokerspring.domain.Game;
import com.silh.planningpokerspring.domain.Player;

import java.util.List;
import java.util.Optional;

public interface GameRepository {

  /**
   * Create a new game, assign ID to it and store it.
   *
   * @param req - creator of the new game.
   * @return - game with id and creator assigned to it.
   */
  Game create(String gameName, Player creator);

  /**
   * Get game by ID.
   *
   * @param id - id of the game.
   * @return - optional which contains found game or empty.
   */
  Optional<Game> find(String id);

  /**
   * Returns list of all games.
   *
   * @return list of all games.
   */
  List<Game> findAll();

  /**
   * Get game by ID and its owner. If game with specified ID is present but owner is different Optional.empty()
   * will be returned.
   *
   * @param id      - id of the game.
   * @param ownerId - id of the owner.
   * @return - optional which contains found game or empty.
   */
  Optional<Game> findByIdAndOwnerId(String id, String ownerId);

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
