package com.silh.planningpokerspring.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class JoinRequest {
  private final String name;

  @JsonCreator
  public JoinRequest(@JsonProperty("name") String name) {
    this.name = name;
  }
}
