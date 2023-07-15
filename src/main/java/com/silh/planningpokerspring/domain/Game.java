package com.silh.planningpokerspring.domain;

import com.silh.planningpokerspring.service.GameEventsSubscriber;
import lombok.Data;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Game {
  private final String id;
  private final String name;
  private final Player creator;
  // Below maps are protected by locks in the service. TODO not ideal
  private final Map<String, Player> players = new HashMap<>();
  private final Map<String, Long> votes = new HashMap<>();
  private final List<GameEventsSubscriber> eventsSubscribers;

  private final Instant createdAt = Instant.now();
  private GameState state = GameState.NOT_STARTED;

  public Game(String id, String name, Player creator) {
    this(id, name, creator, List.of());
  }

  public Game(String id, String name, Player creator, List<GameEventsSubscriber> eventsSubscribers) {
    this.creator = creator;
    this.name = name;
    this.id = id;
    this.eventsSubscribers = eventsSubscribers;
  }

  /**
   * Move game to the next state.
   *
   * @param nextState - state to which we move.
   */
  public void transitionTo(GameState nextState) {
    // Simple implementation, transition to any state is possible.
    this.state = nextState;
    if (nextState == GameState.VOTING
      || nextState == GameState.NOT_STARTED) {
      votes.clear();
    }
  }

  /**
   * Add participant to the game.
   *
   * @param player - new participant.
   * @return - if participant was added.
   */
  public boolean addParticipant(Player player) {
    return players.putIfAbsent(player.id(), player) == null;
  }

  /**
   * Remove participant of the game.
   *
   * @param playerId - ID of a player to remove.
   * @return - if participant was removed.
   */
  public boolean removeParticipant(String playerId) {
    return players.remove(playerId) != null;
  }

  /**
   * Add vote of a player to votes.
   *
   * @param voterId - ID of a voting person.
   * @param value   - vote value.
   * @return - true if vote was accepted, false otherwise.
   */
  public boolean addVote(String voterId, Long value) {
    if (players.get(voterId) == null) {
      return false;
    }
    votes.put(voterId, value);
    return true;
  }

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
   * Returns a copy of the votes
   *
   * @return - copy of game's votes.
   */
  public Map<String, Long> getVotes() {
    return new HashMap<>(votes);
  }
}
