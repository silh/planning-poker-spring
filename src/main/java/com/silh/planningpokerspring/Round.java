package com.silh.planningpokerspring;

import lombok.Getter;
import lombok.ToString;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Getter
@ToString
public class Round {
  private final AtomicReference<RoundState> state = new AtomicReference<>(RoundState.NOT_STARTED);

  private final Map<String, Long> votes = new ConcurrentHashMap<>();

  public Round() {
  }

  public boolean start() {
    return state.compareAndSet(RoundState.NOT_STARTED, RoundState.VOTING);
  }

  public boolean discuss() {
    return state.compareAndSet(RoundState.VOTING, RoundState.DISCUSSION);
  }

  public boolean returnToVoting() {
    final boolean returned = state.compareAndSet(RoundState.DISCUSSION, RoundState.VOTING);
    if (returned) {
      votes.clear();
    }
    return returned;
  }

  public boolean finish() {
    return state.compareAndSet(RoundState.DISCUSSION, RoundState.FINISHED);
  }

  public boolean vote(String voterId, Long value) {
    return votes.putIfAbsent(voterId, value) == null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Round round = (Round) o;
    return state.get().equals(round.state.get()) &&
      votes.equals(round.votes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(state, votes);
  }
}
