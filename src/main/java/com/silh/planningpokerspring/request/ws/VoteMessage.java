package com.silh.planningpokerspring.request.ws;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * A message sent when user tries to vote.
 */
@Getter
public final class VoteMessage extends WsMessage<VoteMessageData> {

  @JsonCreator
  public VoteMessage(@JsonProperty("data") VoteMessageData data) {
    super(IncomingChannel.VOTE, data);
  }
}
