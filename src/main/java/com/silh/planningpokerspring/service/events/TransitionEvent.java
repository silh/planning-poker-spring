package com.silh.planningpokerspring.service.events;

import com.silh.planningpokerspring.domain.GameState;

import java.util.Map;

public record TransitionEvent(String gameId, GameState targetState, Map<String, String> votes) implements GameEvent {
}
