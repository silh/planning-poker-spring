package com.silh.planningpokerspring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.silh.planningpokerspring.domain.GameState;
import com.silh.planningpokerspring.request.GameDto;
import com.silh.planningpokerspring.request.PlayerDto;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class GameWsHandlerTest {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @Test
  void connectAndReceiveNotifications() throws Exception {
    final var gameWsHandler = new GameWsHandler(OBJECT_MAPPER);
    final var webSocketSession = mock(WebSocketSession.class);
    final var textMessage = new TextMessage("""
      {
        "channel": "join",
        "data": {
          "gameId": "game"
        }
      }
      """);
    gameWsHandler.handleTextMessage(webSocketSession, textMessage);

    final var gameId = "game";
    final var gameDto = gameDto(gameId);
    gameWsHandler.notify(gameId, gameDto);

    TimeUnit.SECONDS.sleep(2); // this works but it is slow, better separate those things.
    verify(webSocketSession).sendMessage(any(TextMessage.class));
  }

  private GameDto gameDto(String gameId) {
    return new GameDto(
      gameId,
      new PlayerDto(""),
      GameState.VOTING,
      Collections.emptyMap(),
      Collections.emptyMap()
    );
  }
}
