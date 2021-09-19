package com.silh.planningpokerspring.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record VoteRequest(@JsonProperty("value") Long value) {
}
