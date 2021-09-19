package com.silh.planningpokerspring.request;

import com.fasterxml.jackson.annotation.JsonProperty;

//FIXME need player ID to be able to to match player and vote.
public record PlayerDto(@JsonProperty("name") String name) {
}
