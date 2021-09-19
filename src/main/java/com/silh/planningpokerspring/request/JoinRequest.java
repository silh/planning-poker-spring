package com.silh.planningpokerspring.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record JoinRequest(@JsonProperty("name") String name) {
}
