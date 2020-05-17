package com.silh.planningpokerspring;

import lombok.Data;

@Data
public class Player {
  private String id;
  private String name;

  public Player() {
  }

  public Player(String id, String name) {
    this.id = id;
    this.name = name;
  }
}
