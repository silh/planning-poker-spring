package com.silh.planningpokerspring.service.events;

public record PlayerLeftEvent(String gameId, String playerId) implements GameEvent {
}
