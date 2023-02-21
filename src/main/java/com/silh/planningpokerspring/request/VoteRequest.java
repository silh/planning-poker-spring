package com.silh.planningpokerspring.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record VoteRequest(@JsonProperty("playerId") String playerId, @JsonProperty("value") Long value) {
}
