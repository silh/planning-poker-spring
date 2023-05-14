package com.silh.planningpokerspring.request.ws;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A message sent right after connection to WebSocket. Without it WebSocket session won't receive updates.
 */
public record JoinMessage(@JsonProperty("gameId") String gameId,
                          @JsonProperty("playerId") String playerId) implements WsMessage {
}
