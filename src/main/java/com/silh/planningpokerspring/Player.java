package com.silh.planningpokerspring;

import lombok.Data;

@Data
public class Player {
  private final String id;
  private final String name;

  public Player(String id, String name) {
    this.id = id;
    this.name = name;
  }
}
