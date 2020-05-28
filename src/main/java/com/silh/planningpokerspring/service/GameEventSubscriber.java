package com.silh.planningpokerspring.service;

/**
 * Common interface for games' updates subscribers
 */
public interface GameEventSubscriber {
  /**
   * Receive notification about game update.
   *
   * @param gameId - ID of updated game.
   */
  void notify(String gameId);
}
