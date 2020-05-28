package com.silh.planningpokerspring.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NewGameRequest {
  private final String creatorName;

  @JsonCreator
  public NewGameRequest(
    @JsonProperty("creatorName") String creatorName
  ) {
    this.creatorName = creatorName;
  }
}
