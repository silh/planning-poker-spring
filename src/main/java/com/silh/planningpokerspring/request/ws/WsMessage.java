package com.silh.planningpokerspring.request.ws;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * A wrapper for messages sent over WebSocket channel to the client or received from it.
 */
@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "channel")
@JsonSubTypes({
  @JsonSubTypes.Type(value = JoinMessage.class, name = "join"),
  @JsonSubTypes.Type(value = VoteMessage.class, name = "vote")
})
public sealed interface WsMessage permits JoinMessage, VoteMessage {
}
