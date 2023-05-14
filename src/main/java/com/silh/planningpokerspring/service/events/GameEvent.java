package com.silh.planningpokerspring.service.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "channel")
@JsonSubTypes({
  @JsonSubTypes.Type(value = PlayerJoinedEvent.class, name = "join"),
  @JsonSubTypes.Type(value = VoteEvent.class, name = "vote"),
  @JsonSubTypes.Type(value = TransitionEvent.class, name = "transition"),
  @JsonSubTypes.Type(value = PlayerLeftEvent.class, name = "leave")
})
public sealed interface GameEvent permits PlayerJoinedEvent, PlayerLeftEvent, TransitionEvent, VoteEvent {
  String gameId();
}
