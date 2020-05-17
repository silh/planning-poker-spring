package com.silh.planningpokerspring.service;

import com.silh.planningpokerspring.Game;
import com.silh.planningpokerspring.Player;
import com.silh.planningpokerspring.RoundState;

import java.util.Optional;

public interface GameService {
  /**
   * Create a new game.
   *
   * @param creator - creator of the new game.
   * @return - created Game.
   */
  Game createGame(Player creator);

  /**
   * Try moving the game's round to {@link RoundState#VOTING}.
   * Only creator can successfully execute this.
   *
   * @param gameId   - ID of the game which round should to start.
   * @param personId - ID of the person trying to start a round.
   * @return - true if game was started, false otherwise.
   */
  boolean startRound(String gameId, String personId);

  /**
   * Try moving the game's round to {@link RoundState#DISCUSSION} from {@link}.
   * Only creator can successfully execute this.
   *
   * @param gameId   - ID of the game which round should be moved to voting stage.
   * @param personId - ID of the person trying to move round to a voting stage.
   * @return - true if the game was moved to voting.
   */
  boolean startDiscussion(String gameId, String personId);

  /**
   * Try moving the game's round back to {@link RoundState#VOTING} from {@link RoundState#DISCUSSION}.
   * Only creator can successfully execute this.
   *
   * @param gameId   - ID of the game which round should be moved to voting voting stage.
   * @param personId - ID of the person trying to move round to a voting stage.
   * @return - true if the game was moved to voting.
   */
  boolean restartVoting(String gameId, String personId);

  /**
   * Try moving the game's round back to {@link RoundState#FINISHED} from {@link RoundState#DISCUSSION}.
   * Only creator can successfully execute this.
   *
   * @param gameId   - ID of the game which round should be moved to voting voting stage.
   * @param personId - ID of the person trying to move round to a voting stage.
   * @return - true if the game was moved to voting.
   */
  boolean finishRound(String gameId, String personId);

  boolean vote(String gameId, String voterId, Long value);

  Optional<Game> getGame(String gameId);
}
