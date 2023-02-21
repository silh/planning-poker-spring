package com.silh.planningpokerspring.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NewGameRequest(
  @JsonProperty("gameName") String gameName,
  @JsonProperty("creatorId") String creatorId) {
}
