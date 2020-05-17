package com.silh.planningpokerspring;

import ch.qos.logback.core.joran.spi.EventPlayer;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
@AllArgsConstructor
public class Game {
  private final String id;
  private final Player creator;
  private final List<EventPlayer> participants;

  public Game(String id, Player creator) {
    this.creator = creator;
    this.id = id;
    participants = new CopyOnWriteArrayList<>();
  }
}
