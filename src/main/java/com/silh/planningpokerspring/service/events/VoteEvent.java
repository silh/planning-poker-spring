package com.silh.planningpokerspring.service.events;

public record VoteEvent(String gameId, String playerId, long vote) implements GameEvent {
}
