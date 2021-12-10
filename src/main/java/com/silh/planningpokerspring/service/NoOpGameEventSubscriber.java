package com.silh.planningpokerspring.service;

import com.silh.planningpokerspring.request.GameDto;
import org.springframework.lang.NonNull;

/**
 * Placeholder implementation of GameEventSubscriber.
 */
public class NoOpGameEventSubscriber implements GameEventsSubscriber {

  @Override
  public void notify(@NonNull String gameId, @NonNull GameDto gameDto) {

  }
}
