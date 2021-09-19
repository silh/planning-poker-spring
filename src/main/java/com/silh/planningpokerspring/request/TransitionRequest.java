package com.silh.planningpokerspring.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.silh.planningpokerspring.domain.GameState;

public record TransitionRequest(@JsonProperty("nextState") GameState nextState) {
}
