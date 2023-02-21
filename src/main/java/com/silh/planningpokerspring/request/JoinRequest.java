package com.silh.planningpokerspring.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record JoinRequest(@JsonProperty("playerId") String playerId) {
}
