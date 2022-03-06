package com.silh.planningpokerspring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.silh.planningpokerspring.request.GameDto;
import com.silh.planningpokerspring.request.ws.JoinMessage;
import com.silh.planningpokerspring.request.ws.VoteMessage;
import com.silh.planningpokerspring.request.ws.WsMessage;
import com.silh.planningpokerspring.service.GameEventsSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Responsible for sending game state update events to the client.
 */
@Slf4j
public class GameWsHandler extends TextWebSocketHandler
  implements GameEventsSubscriber {

  private final ObjectMapper objectMapper;

  private final ConcurrentMap<String, Set<WebSocketSession>> gameIdToParticipants = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, GameDto> pendingNotifications = new ConcurrentHashMap<>();
  // FIXME this is ugly, find a better way to do that
  private final ScheduledExecutorService executorService;
  private final Duration sendDelay;

  public GameWsHandler(ObjectMapper objectMapper, ScheduledExecutorService executorService, Duration sendDelay) {
    this.objectMapper = objectMapper;
    this.executorService = executorService;
    this.sendDelay = sendDelay;
  }

  @Override
  public void afterConnectionEstablished(@NonNull WebSocketSession session) {
    log.debug("New connection from {}.", session.getRemoteAddress());
  }

  @Override
  protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
    final String payload = message.getPayload();
    final WsMessage<?> wsMessage = objectMapper.readValue(payload, WsMessage.class);
    switch (wsMessage) {
      case JoinMessage j -> addSession(session, j);
      case VoteMessage v -> log.info("Should we even have that?");
    }
  }

  private void addSession(WebSocketSession session, JoinMessage j) {
    gameIdToParticipants.compute(j.getData().gameId(), (key, value) -> {
      if (value == null) {
        value = ConcurrentHashMap.newKeySet();
      }
      value.add(session);
      return value;
    });
  }

  /**
   * Puts a notification into pending notifications to be processed at some time in the future.
   */
  @Override
  public void notify(@NonNull GameDto gameDto) {
    pendingNotifications.compute(gameDto.id(), (key, value) -> {
      if (value == null) {
        executorService.schedule(() -> notifyGameParticipants(gameDto.id()), sendDelay.toMillis(), TimeUnit.MILLISECONDS);
      }
      return gameDto;
    });
  }

  private void notifyGameParticipants(String gameId) {
    gameIdToParticipants.compute(gameId, (id, webSocketSessions) -> {
      final var gameDto = pendingNotifications.remove(gameId);
      if (webSocketSessions == null || gameDto == null) {
        return webSocketSessions;
      }
      for (WebSocketSession webSocketSession : webSocketSessions) {
        try {
          webSocketSession.sendMessage(new TextMessage(objectMapper.writeValueAsBytes(gameDto)));
        } catch (Exception e) {
          log.warn("Wasn't able to send update to {}: ", webSocketSession.getRemoteAddress(), e);
        }
      }
      return webSocketSessions;
    });
  }

}
