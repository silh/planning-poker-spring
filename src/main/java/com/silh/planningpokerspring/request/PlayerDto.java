package com.silh.planningpokerspring.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PlayerDto(
  @JsonProperty("id") String id,
  @JsonProperty("name") String name) {
}
