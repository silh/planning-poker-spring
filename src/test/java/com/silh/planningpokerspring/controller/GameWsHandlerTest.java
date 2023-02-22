package com.silh.planningpokerspring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.silh.planningpokerspring.domain.GameState;
import com.silh.planningpokerspring.request.GameDto;
import com.silh.planningpokerspring.request.PlayerDto;
import com.silh.planningpokerspring.service.GameService;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GameWsHandlerTest {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final Duration SEND_DELAY = Duration.ofMillis(10L);

  private final GameService mockGameService = mock(GameService.class);
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void connectAndReceiveNotifications() throws Exception {
    final var gameWsHandler =
      new GameWsHandler(OBJECT_MAPPER, mockGameService, Executors.newSingleThreadScheduledExecutor(), SEND_DELAY);
    final var webSocketSession = mock(WebSocketSession.class);
    when(mockGameService.joinGame("game", "playerId")).thenReturn(true);
    final var textMessage = new TextMessage("""
      {
        "channel": "join",
        "gameId": "game",
        "playerId": "playerId"
      }
      """);
    gameWsHandler.handleTextMessage(webSocketSession, textMessage);

    // prepare a way to get notified when send is executed
    CompletableFuture<TextMessage> futureNotification = new CompletableFuture<>();
    doAnswer(a -> {
      futureNotification.complete(a.getArgument(0));
      return null;
    }).when(webSocketSession).sendMessage(any(TextMessage.class));
    final var expectedGame = gameDto("game");
    gameWsHandler.notify(expectedGame);
    GameDto notificationGame = objectMapper.readValue(
      futureNotification.get(5 * SEND_DELAY.toMillis(), TimeUnit.MILLISECONDS).getPayload(),
      GameDto.class);
    assertThat(notificationGame).isEqualTo(expectedGame);
  }

  private GameDto gameDto(String gameId) {
    return new GameDto(
      gameId,
      new PlayerDto("creator", "creator"),
      GameState.VOTING,
      Collections.emptyMap(),
      Collections.emptyMap()
    );
  }
}
