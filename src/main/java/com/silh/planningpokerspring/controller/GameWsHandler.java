package com.silh.planningpokerspring.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.silh.planningpokerspring.request.ws.JoinMessage;
import com.silh.planningpokerspring.request.ws.VoteMessage;
import com.silh.planningpokerspring.request.ws.WsMessage;
import com.silh.planningpokerspring.service.GameEventsSubscriber;
import com.silh.planningpokerspring.service.GameService;
import com.silh.planningpokerspring.service.events.GameEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Responsible for sending game state update events to the client.
 */
@Slf4j
public class GameWsHandler extends TextWebSocketHandler
  implements GameEventsSubscriber {

  private final ObjectMapper objectMapper;

  // TODO the bellow 2 fields should be most likely protected by one lock as otherwise there is a possibility for a race
  // condition.
  private final ConcurrentMap<String, Set<WebSocketSession>> gameIdToParticipants = new ConcurrentHashMap<>();
  private final ConcurrentMap<WebSocketSession, JoinMessage> sessionToSessionInfo = new ConcurrentHashMap<>();

  private final GameService gameService;

  public GameWsHandler(ObjectMapper objectMapper, GameService gameService) {
    this.objectMapper = objectMapper;
    this.gameService = gameService;
    // FIXME this is ugly, find a better way to do that
    this.gameService.subscribe(this);
  }

  @Override
  public void afterConnectionEstablished(@NonNull WebSocketSession session) {
    log.debug("New connection from {}.", session.getRemoteAddress());
  }

  @Override
  protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
    final String payload = message.getPayload();
    switch (objectMapper.readValue(payload, WsMessage.class)) {
      case JoinMessage j -> addSession(session, j);
      case VoteMessage v -> log.info("Should we even have that?");
    }
  }

  private void addSession(WebSocketSession session, JoinMessage joinMessage) {
    var oldMessage = sessionToSessionInfo.putIfAbsent(session, joinMessage);
    if (oldMessage != null) {
      log.warn("Client {} already participates in a game, not joining: sessionInfo={}",
        session.getRemoteAddress(), oldMessage);
      return;
    }
    gameIdToParticipants.computeIfAbsent(
        joinMessage.gameId(),
        k -> ConcurrentHashMap.newKeySet()
      )
      .add(session);
    boolean joined = gameService.joinGame(joinMessage.gameId(), joinMessage.playerId());
    if (!joined) {
      log.warn("Could not join the game: session={}, message={}", session.getRemoteAddress(), joinMessage);
      cleanLocalMaps(session);
    }
  }

  @Override
  // TODO I think I should rather create a some self-handling game instance for each game
  public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
    JoinMessage joinMessage = cleanLocalMaps(session);
    if (joinMessage == null) return;
    gameService.leaveGame(joinMessage.gameId(), joinMessage.playerId());
  }

  private JoinMessage cleanLocalMaps(WebSocketSession session) {
    JoinMessage joinMessage = sessionToSessionInfo.remove(session);
    if (joinMessage == null) {
      return null;
    }
    gameIdToParticipants.compute(joinMessage.gameId(), (k, sessions) -> {
      if (sessions == null) {
        return null;
      }
      sessions.remove(session);
      return sessions.isEmpty() ? null : sessions;
    });
    return joinMessage;
  }

  /**
   * Puts a notification into pending notifications to be processed at some time in the future.
   */
  @Override
  public void notify(@NonNull GameEvent gameEvent) {
    try {
      var payload = objectMapper.writeValueAsBytes(gameEvent);

      gameIdToParticipants.get(gameEvent.gameId())
        .forEach(wsSession -> {
          try {
            wsSession.sendMessage(new TextMessage(payload));
          } catch (Exception e) {
            log.warn("Wasn't able to send update to {}: ", wsSession.getRemoteAddress(), e);
          }
        });
    } catch (JsonProcessingException e) {
      log.error("Wasn't able to convert event {}: ", gameEvent, e);
    }
  }

}
