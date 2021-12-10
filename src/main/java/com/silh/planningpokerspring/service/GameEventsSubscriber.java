package com.silh.planningpokerspring.service;

import com.silh.planningpokerspring.request.GameDto;
import org.springframework.lang.NonNull;

/**
 * Common interface for games' updates subscribers
 */
public interface GameEventsSubscriber {
  /**
   * Receive notification about game update.
   *
   * @param gameId - ID of updated game.
   * @param game   - game state.
   */
  void notify(@NonNull String gameId, @NonNull GameDto game);
}
