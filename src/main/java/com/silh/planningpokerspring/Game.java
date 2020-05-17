package com.silh.planningpokerspring;

import ch.qos.logback.core.joran.spi.EventPlayer;
import lombok.Data;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
public class Game {
  private String id;
  private Player creator;
  private List<EventPlayer> participants;
  private Round round = new Round();

  public Game() {
  }

  public Game(String id, Player creator) {
    this.creator = creator;
    this.id = id;
    participants = new CopyOnWriteArrayList<>();
  }

  public Game(String id, Player creator, List<EventPlayer> participants) {
    this.id = id;
    this.creator = creator;
    this.participants = participants;
  }
}
