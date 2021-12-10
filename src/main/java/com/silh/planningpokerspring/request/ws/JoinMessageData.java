package com.silh.planningpokerspring.request.ws;

import com.fasterxml.jackson.annotation.JsonProperty;

public record JoinMessageData(@JsonProperty("gameId") String gameId) {
}
