package com.silh.planningpokerspring.request.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WsMessageTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void canDeserealizeJoinMessage() throws JsonProcessingException {
    final var json = """
      {
        "channel" : "join",
        "data" : {
          "name": "user"
        }
      }
      """;
    final WsMessage<?> wsMessage = objectMapper.readValue(json, WsMessage.class);
    assertThat(wsMessage).isInstanceOf(WsMessage.JoinMessage.class);
    assertThat(wsMessage.getChannel()).isEqualTo(IncomingChannel.JOIN);
    assertThat(wsMessage.getData()).isEqualTo(new JoinMessageData("user"));
  }

  @Test
  void canDeserealizeVoteMessage() throws JsonProcessingException {
    final var json = """
      {
        "channel" : "vote",
        "data" : {
          "vote": 1
        }
      }
      """;
    final WsMessage<?> wsMessage = objectMapper.readValue(json, WsMessage.class);
    assertThat(wsMessage).isInstanceOf(WsMessage.VoteMessage.class);
    assertThat(wsMessage.getChannel()).isEqualTo(IncomingChannel.VOTE);
    assertThat(wsMessage.getData()).isEqualTo(new VoteMessageData(1L));
  }
}
