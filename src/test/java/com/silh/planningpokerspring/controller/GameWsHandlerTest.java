package com.silh.planningpokerspring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.silh.planningpokerspring.request.PlayerDto;
import com.silh.planningpokerspring.service.GameService;
import com.silh.planningpokerspring.service.events.GameEvent;
import com.silh.planningpokerspring.service.events.PlayerJoinedEvent;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GameWsHandlerTest {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private final GameService mockGameService = mock(GameService.class);

  @Test
  void connectAndReceiveNotifications() throws Exception {
    final var gameWsHandler = new GameWsHandler(OBJECT_MAPPER, mockGameService);
    final var webSocketSession = mock(WebSocketSession.class);
    String playerId = "playerId";
    final var gameId = "gameId";
    when(mockGameService.joinGame(gameId, playerId)).thenReturn(true);
    final var textMessage = new TextMessage(String.format("""
      {
        "channel": "join",
        "gameId": "%s",
        "playerId": "%s"
      }
      """, gameId, playerId));
    gameWsHandler.handleTextMessage(webSocketSession, textMessage);

    // prepare a way to get notified when send is executed
    CompletableFuture<TextMessage> futureNotification = new CompletableFuture<>();
    doAnswer(a -> {
      futureNotification.complete(a.getArgument(0));
      return null;
    }).when(webSocketSession).sendMessage(any(TextMessage.class));

    // check the PlayerJoinedEvent
    PlayerDto expectedPlayer = new PlayerDto(playerId, "name");
    gameWsHandler.notify(new PlayerJoinedEvent(gameId, expectedPlayer));
    GameEvent gameEvent = OBJECT_MAPPER.readValue(
      futureNotification.get(1, TimeUnit.SECONDS).getPayload(),
      GameEvent.class
    );
    assertThat(gameEvent)
      .asInstanceOf(InstanceOfAssertFactories.type(PlayerJoinedEvent.class))
      .isEqualTo(new PlayerJoinedEvent(gameId, expectedPlayer));
  }

}
