package com.silh.planningpokerspring.repository;

import com.silh.planningpokerspring.Game;
import com.silh.planningpokerspring.Player;
import org.hashids.Hashids;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class ConcurrentHashMapGameRepository implements GameRepository {

  private final ConcurrentHashMap<String, Game> games = new ConcurrentHashMap<>();
  private final Hashids idGenerator = new Hashids("add some salt, per favore");

  /**
   * Create a new game, assign ID to it and store it.
   *
   * @param creator - creator of the new game.
   * @return - game with id and creator assigned to it.
   */
  @Override
  public Game create(Player creator) {
    return insertNewGame(creator);
  }


  @Override
  public Optional<Game> find(String id) {
    return Optional.ofNullable(games.get(id));
  }

  @Override
  public Optional<Game> findByIdAndOwnerId(String id, String ownerId) {
    return Optional.ofNullable(games.get(id))
      .filter(game -> game.getCreator().getId().equals(ownerId));
  }

  @Override
  public Optional<Game> update(Game updatedGame) {
    return Optional.ofNullable(games.compute(updatedGame.getId(), (id, game) -> {
      if (game == null) {
        return null;
      }
      return updatedGame;
    }));
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

  private Game insertNewGame(Player creator) {
    Game game;
    Game oldGame;
    do {
      final String newId = getNewId();
      game = new Game(newId, creator);
      oldGame = games.putIfAbsent(newId, game);
    } while (oldGame != null);
    return game;
  }

  private String getNewId() {
    final ThreadLocalRandom random = ThreadLocalRandom.current();
    return idGenerator.encode(random.nextLong(Hashids.MAX_NUMBER),
      random.nextLong(Hashids.MAX_NUMBER),
      random.nextLong(Hashids.MAX_NUMBER));
  }
}
