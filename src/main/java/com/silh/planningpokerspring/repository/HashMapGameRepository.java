package com.silh.planningpokerspring.repository;

import com.silh.planningpokerspring.domain.Game;
import com.silh.planningpokerspring.domain.Player;
import com.silh.planningpokerspring.service.StringIdGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Expected to be used only by one thread at a time.
 */
public class HashMapGameRepository implements GameRepository {

  private final HashMap<String, Game> games = new HashMap<>();
  private final StringIdGenerator idGenerator;

  public HashMapGameRepository(StringIdGenerator idGenerator) {
    this.idGenerator = idGenerator;
  }

  @Override
  public Game create(String gameName, Player creator) {
    return insertNewGame(gameName, creator);
  }

  @Override
  public Optional<Game> find(String id) {
    return Optional.ofNullable(games.get(id));
  }

  @Override
  public List<Game> findAll() {
    // TODO this is unsafe and should be fixed.
    return new ArrayList<>(games.values());
  }

  @Override
  public Optional<Game> findByIdAndOwnerId(String id, String ownerId) {
    return Optional.ofNullable(games.get(id))
      .filter(game -> game.getCreator().id().equals(ownerId));
  }

  @Override
  public Optional<Game> update(Game updatedGame) {
    return Optional.ofNullable(games.computeIfPresent(updatedGame.getId(), (id, game) -> updatedGame));
  }

  /**
   * Delete exiting game by ID.
   *
   * @param id - id of the game to delete.
   */
  @Override
  public void delete(String id) {
    games.remove(id);
  }

  private Game insertNewGame(String gameName, Player cretor) {
    Game game;
    Game oldGame;
    do {
      final String newId = idGenerator.generate();
      game = new Game(newId, gameName, cretor);
      oldGame = games.putIfAbsent(newId, game);
    } while (oldGame != null);
    return game;
  }
}
