package com.silh.planningpokerspring.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NewGameRequest(@JsonProperty("name") String name) {
}
