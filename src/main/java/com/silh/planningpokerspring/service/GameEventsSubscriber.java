package com.silh.planningpokerspring.service;

import com.silh.planningpokerspring.service.events.GameEvent;
import org.springframework.context.event.EventListener;
import org.springframework.lang.NonNull;

/**
 * Common interface for games' updates subscribers
 */
public interface GameEventsSubscriber {
  /**
   * Receive notification about game update.
   *
   * @param gameEvent - what happened.
   */
  @EventListener(GameEvent.class)
  void notify(@NonNull GameEvent gameEvent);
}
