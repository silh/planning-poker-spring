package com.silh.planningpokerspring.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VoteRequest {
  private final Long value;

  @JsonCreator
  public VoteRequest(@JsonProperty("value") Long value) {
    this.value = value;
  }
}
