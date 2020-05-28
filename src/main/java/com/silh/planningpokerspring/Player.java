package com.silh.planningpokerspring;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;

@Data
public class Player {
  private final String id;
  private final String name;

  @JsonCreator
  public Player(String id, String name) {
    this.id = id;
    this.name = name;
  }
}
