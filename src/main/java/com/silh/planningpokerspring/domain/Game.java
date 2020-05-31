package com.silh.planningpokerspring.domain;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Data
public class Game {
  private final String id;
  private final Player creator;
  private final Map<String, Player> participants = new ConcurrentHashMap<>();
  private final Map<String, Long> votes = new ConcurrentHashMap<>();
  private final AtomicReference<GameState> state = new AtomicReference<>(GameState.NOT_STARTED);

  public Game(String id, Player creator) {
    this.creator = creator;
    this.id = id;
  }

  /**
   * Move game to the next state.
   *
   * @param nextState - state to which we move.
   */
  public void transitionTo(GameState nextState) {
    // Simple implementation, transition to any state is possible.
    state.set(nextState);
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
    return participants.putIfAbsent(player.getId(), player) == null;
  }

  /**
   * Add vote of a player to votes.
   *
   * @param voterId - ID of a voting person.
   * @param value   - vote value.
   * @return - true if vote was accepted, false otherwise.
   */
  public boolean addVote(String voterId, Long value) {
    //TODO check that player belongs to a game.
    return votes.putIfAbsent(voterId, value) == null;
  }

  /**
   * Get current game state unwrapped from AtomicReference.
   *
   * @return - current state of the game.
   */
  public GameState getState() {
    return state.get();
  }

  /**
   * Returns a copy of participants.
   *
   * @return - copy of game's participants.
   */
  public Map<String, Player> getParticipants() {
    return new HashMap<>(participants);
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
