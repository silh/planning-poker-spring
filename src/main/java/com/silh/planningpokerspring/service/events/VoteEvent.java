package com.silh.planningpokerspring.service.events;

/**
 * Issued only during VOTING state, so it never actually has a vote in it.
 *
 * @param gameId   - id of a game where event has happened.
 * @param playerId - player that voted.
 */
public record VoteEvent(String gameId, String playerId) implements GameEvent {
}
