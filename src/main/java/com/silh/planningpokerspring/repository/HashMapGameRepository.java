package com.silh.planningpokerspring.repository;

import com.silh.planningpokerspring.converter.PlayerConverter;
import com.silh.planningpokerspring.domain.Game;
import com.silh.planningpokerspring.domain.Player;
import com.silh.planningpokerspring.service.StringIdGenerator;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Expected to be used only by one thread at a time.
 */
public class HashMapGameRepository implements GameRepository {

  private final ConcurrentMap<String, Game> games = new ConcurrentHashMap<>();
  private final StringIdGenerator idGenerator;
  // TODO The bellow 2 are just to create games, change to factory?
  private final ApplicationEventPublisher eventPublisher;
  private final PlayerConverter playerConverter;

  public HashMapGameRepository(
    StringIdGenerator idGenerator,
    ApplicationEventPublisher eventPublisher,
    PlayerConverter playerConverter) {
    this.idGenerator = idGenerator;
    this.eventPublisher = eventPublisher;
    this.playerConverter = playerConverter;
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

  private Game insertNewGame(String gameName, Player creator) {
    Game game;
    Game oldGame;
    do {
      final String newId = idGenerator.generate();
      game = new Game(newId, gameName, creator, eventPublisher, playerConverter);
      oldGame = games.putIfAbsent(newId, game);
    } while (oldGame != null);
    return game;
  }
}
