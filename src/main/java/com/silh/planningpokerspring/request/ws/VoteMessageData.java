package com.silh.planningpokerspring.request.ws;

import com.fasterxml.jackson.annotation.JsonProperty;

public record VoteMessageData(@JsonProperty("vote") Long vote) { // FIXME should not be vote really
}
