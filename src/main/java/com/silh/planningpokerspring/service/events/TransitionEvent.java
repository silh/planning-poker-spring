package com.silh.planningpokerspring.service.events;

import com.silh.planningpokerspring.domain.GameState;

public record TransitionEvent(String gameId, GameState targetState) implements GameEvent {
}
