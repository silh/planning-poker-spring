package com.silh.planningpokerspring.service.events;

import com.silh.planningpokerspring.request.RoundResultDto;

public record RoundFinishedEvent(String gameId, RoundResultDto roundResult) implements GameEvent {
}
