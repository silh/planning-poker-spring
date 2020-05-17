package com.silh.planningpokerspring.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.silh.planningpokerspring.RoundState;
import lombok.Data;

@Data
public class TransitionRequest {
  private final RoundState nextState;

  @JsonCreator
  public TransitionRequest(
    @JsonProperty("nextState") RoundState nextState) {
    this.nextState = nextState;
  }
}
