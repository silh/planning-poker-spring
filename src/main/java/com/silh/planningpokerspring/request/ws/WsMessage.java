package com.silh.planningpokerspring.request.ws;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A wrapper for messages sent over WebSocket channel to the client or received from it.
 *
 * @param <T>
 */
@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "channel")
@JsonSubTypes({
  @JsonSubTypes.Type(value = WsMessage.JoinMessage.class, name = "join"),
  @JsonSubTypes.Type(value = WsMessage.VoteMessage.class, name = "vote")
})
@AllArgsConstructor
@Getter
public sealed class WsMessage<T> {
  private final IncomingChannel channel;
  private final T data;

  public static final class JoinMessage extends WsMessage<JoinMessageData> {

    @JsonCreator
    public JoinMessage(@JsonProperty("data") JoinMessageData data) {
      super(IncomingChannel.JOIN, data);
    }
  }

  public static final class VoteMessage extends WsMessage<VoteMessageData> {
    @JsonCreator
    public VoteMessage(@JsonProperty("data") VoteMessageData data) {
      super(IncomingChannel.VOTE, data);
    }
  }

}
