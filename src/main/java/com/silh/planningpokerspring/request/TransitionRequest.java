package com.silh.planningpokerspring.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.silh.planningpokerspring.domain.GameState;
import lombok.Data;

@Data
public class TransitionRequest {
  private final GameState nextState;

  @JsonCreator
  public TransitionRequest(
    @JsonProperty("nextState") GameState nextState) {
    this.nextState = nextState;
  }
}
