package com.silh.planningpokerspring.service.events;

public record VoteEvent(String gameId, String playerId, Long vote) implements GameEvent {
}
