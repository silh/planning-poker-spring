package com.silh.planningpokerspring.service.events;

import com.silh.planningpokerspring.request.PlayerDto;

public record PlayerJoinedEvent(String gameId, PlayerDto player) implements GameEvent {
}
