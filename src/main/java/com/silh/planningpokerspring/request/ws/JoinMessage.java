package com.silh.planningpokerspring.request.ws;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * A message sent right after connection to WebSocket. Without it WebSocket session won't receive updates.
 */
@Getter
public final class JoinMessage extends WsMessage<JoinMessageData> {

  @JsonCreator
  public JoinMessage(@JsonProperty("data") JoinMessageData data) {
    super(IncomingChannel.JOIN, data);
  }
}
