package com.silh.planningpokerspring.request.ws;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A message sent when user tries to vote.
 */
public record VoteMessage(@JsonProperty("vote") long vote) implements WsMessage {
}
