package com.silh.planningpokerspring.repository;

import com.silh.planningpokerspring.domain.Player;
import com.silh.planningpokerspring.service.StringIdGenerator;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class UserRepository {

  private final ConcurrentMap<String, Player> players = new ConcurrentHashMap<>();
  private final StringIdGenerator idGenerator;

  public UserRepository(StringIdGenerator idGenerator) {
    this.idGenerator = idGenerator;
  }

  public Optional<Player> find(String id) {
    return Optional.ofNullable(players.get(id));
  }

  public Player create(String name) {
    return insertNewPlayer(name);
  }

  public Optional<Player> get(String id) {
    return Optional.ofNullable(players.get(id));
  }

  private Player insertNewPlayer(String name) {
    Player player;
    Player oldPlayer;
    do {
      final String id = idGenerator.generate();
      player = new Player(id, name);
      oldPlayer = players.putIfAbsent(id, player);
    } while (oldPlayer != null);
    return player;
  }
}
