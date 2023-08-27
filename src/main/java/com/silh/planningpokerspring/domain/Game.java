package com.silh.planningpokerspring.domain;

import com.silh.planningpokerspring.converter.PlayerConverter;
import com.silh.planningpokerspring.service.events.PlayerJoinedEvent;
import com.silh.planningpokerspring.service.events.PlayerLeftEvent;
import com.silh.planningpokerspring.service.events.TransitionEvent;
import com.silh.planningpokerspring.service.events.VoteEvent;
import lombok.Data;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Data
public class Game {
  private final String id;
  private final String name;
  private final Player creator;
  private final Map<String, Player> players = new HashMap<>();
  private final Map<String, String> votes = new HashMap<>();
  private final ApplicationEventPublisher eventPublisher; // TODO this is not async...
  private final PlayerConverter playerConverter;

  private final Instant createdAt = Instant.now();
  private final Executor executor;
  private GameState state = GameState.NOT_STARTED;

  public Game(
    String id,
    String name,
    Player creator,
    ApplicationEventPublisher eventPublisher,
    PlayerConverter playerConverter
  ) {
    this(
      id,
      name,
      creator,
      eventPublisher,
      playerConverter,
      Executors.newSingleThreadExecutor(Thread.ofVirtual().factory())
    );
  }

  public Game(
    String id,
    String name,
    Player creator,
    ApplicationEventPublisher eventPublisher,
    PlayerConverter playerConverter,
    Executor executor // Used in tests
  ) {
    this.creator = creator;
    this.name = name;
    this.id = id;
    this.eventPublisher = eventPublisher;
    this.playerConverter = playerConverter;
    this.executor = executor;
  }

  /**
   * Move game to the next state.
   *
   * @param nextState - state to which we move.
   */
  public void transitionTo(GameState nextState) {
    executor.execute(() -> {
      // Simple implementation, transition to any state is possible.
      this.state = nextState;
      if (nextState == GameState.VOTING
        || nextState == GameState.NOT_STARTED) {
        votes.clear();
      }
      this.eventPublisher.publishEvent(new TransitionEvent(this.id, this.state, this.getVotes()));
    });
  }

  /**
   * Add participant to the game.
   *
   * @param player - new participant.
   */
  public void addParticipant(Player player) {
    executor.execute(() -> {
      players.putIfAbsent(player.id(), player);
      this.eventPublisher.publishEvent(new PlayerJoinedEvent(this.id, playerConverter.convert(player)));
    });
  }

  /**
   * Remove participant of the game.
   *
   * @param playerId - ID of a player to remove.
   */
  public void removeParticipant(String playerId) {
    executor.execute(() -> {
      votes.remove(playerId);
      players.remove(playerId);
      this.eventPublisher.publishEvent(new PlayerLeftEvent(this.id, playerId));
    });
  }

  /**
   * Add vote of a player to votes.
   *
   * @param voterId - ID of a voting person.
   * @param value   - vote value.
   */
  public void addVote(String voterId, String value) {
    executor.execute(() -> {
      if (state != GameState.VOTING || players.get(voterId) == null) {
        return;
      }
      votes.put(voterId, value);
      this.eventPublisher.publishEvent(new VoteEvent(id, voterId));
    });
  }

  // TODO Below methods should be reworked to avoid concurrency problems

  /**
   * Get current game state unwrapped from AtomicReference.
   *
   * @return - current state of the game.
   */
  public GameState getState() {
    return state;
  }

  /**
   * Returns a copy of players.
   *
   * @return - copy of game's players.
   */
  public Map<String, Player> getPlayers() {
    return new HashMap<>(players);
  }

  /**
   * Returns a copy of the votes. If the game state is VOTING - all votes are hidden.
   *
   * @return - copy of game's votes.
   */
  public Map<String, String> getVotes() {
    if (state == GameState.VOTING) {
      return votes.entrySet()
        .stream()
        .collect(Collectors.toMap(
          Map.Entry::getKey, e -> "*"
        ));
    }
    return new HashMap<>(votes);
  }
}
