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
        "gameId": "id"
      }
      """;
    final WsMessage wsMessage = objectMapper.readValue(json, WsMessage.class);
    assertThat(wsMessage).isInstanceOf(JoinMessage.class);
    JoinMessage joinMessage = (JoinMessage) wsMessage;
    assertThat(joinMessage).isEqualTo(new JoinMessage("id"));
  }

  @Test
  void canDeserealizeVoteMessage() throws JsonProcessingException {
    final var json = """
      {
        "channel" : "vote",
        "vote": 1
      }
      """;
    final WsMessage wsMessage = objectMapper.readValue(json, WsMessage.class);
    assertThat(wsMessage).isInstanceOf(VoteMessage.class);
    var voteMessage = (VoteMessage) wsMessage;
    assertThat(voteMessage).isEqualTo(new VoteMessage(1L));
  }
}
