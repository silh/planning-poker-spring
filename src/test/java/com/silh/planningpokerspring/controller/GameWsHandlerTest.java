package com.silh.planningpokerspring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.silh.planningpokerspring.domain.GameState;
import com.silh.planningpokerspring.request.GameDto;
import com.silh.planningpokerspring.request.PlayerDto;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

class GameWsHandlerTest {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final Duration SEND_DELAY = Duration.ofMillis(10L);

  @Test
  void connectAndReceiveNotifications() throws Exception {
    final var gameWsHandler = new GameWsHandler(OBJECT_MAPPER, Executors.newSingleThreadScheduledExecutor(), SEND_DELAY);
    final var webSocketSession = mock(WebSocketSession.class);
    // prepare a way to get notified when send is executed
    final var sendExecuted = new CountDownLatch(1);
    doAnswer(a -> {
      sendExecuted.countDown();
      return null;
    }).when(webSocketSession).sendMessage(any(TextMessage.class));
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
    gameWsHandler.notify(gameDto);

    assertThat(sendExecuted.await(2 * SEND_DELAY.toMillis(), TimeUnit.MILLISECONDS)).isTrue();
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
