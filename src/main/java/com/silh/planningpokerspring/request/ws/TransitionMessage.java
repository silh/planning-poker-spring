package com.silh.planningpokerspring.request.ws;

import com.silh.planningpokerspring.domain.GameState;

public record TransitionMessage(GameState nextState) implements WsMessage {
}
