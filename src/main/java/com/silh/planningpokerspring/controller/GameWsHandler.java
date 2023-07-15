package com.silh.planningpokerspring.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.silh.planningpokerspring.request.ws.JoinMessage;
import com.silh.planningpokerspring.request.ws.TransitionMessage;
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

import java.util.Collections;
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
  private final GameService gameService;

  // TODO the bellow 2 fields should be most likely protected by one lock as otherwise there is a possibility for a race
  // condition.
  private final ConcurrentMap<String, Set<WebSocketSession>> gameIdToParticipants = new ConcurrentHashMap<>();
  private final ConcurrentMap<WebSocketSession, JoinMessage> sessionToSessionInfo = new ConcurrentHashMap<>();


  public GameWsHandler(ObjectMapper objectMapper, GameService gameService) {
    this.objectMapper = objectMapper;
    this.gameService = gameService;
  }

  @Override
  public void afterConnectionEstablished(@NonNull WebSocketSession session) {
    log.debug("New connection from {}.", session.getRemoteAddress());
  }

  @Override
  protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
    try {
      final String payload = message.getPayload();
      switch (objectMapper.readValue(payload, WsMessage.class)) {
        case JoinMessage j -> addSession(session, j);
        case VoteMessage v -> vote(session, v);
        case TransitionMessage v -> transition(session, v);
      }
    } catch (Exception e) {
      log.info("Couldn't handle the message={}, errorMessage={}", message, e.getMessage());
    }
  }

  private void transition(WebSocketSession session, TransitionMessage transitionMessage) {
    try {
      log.debug("Transitioning: message={}", transitionMessage);
      var sessionInfo = sessionToSessionInfo.get(session);
      if (sessionInfo == null) return;
      gameService.transitionTo(sessionInfo.gameId(), sessionInfo.playerId(), transitionMessage.nextState());
    } catch (RuntimeException e) {
      log.error("Exception while transitioning request={}: ", transitionMessage, e);
    }
  }

  private void vote(WebSocketSession session, VoteMessage voteMessage) {
    try {
      var sessionInfo = sessionToSessionInfo.get(session);
      if (sessionInfo == null) return;
      gameService.vote(sessionInfo.gameId(), sessionInfo.playerId(), voteMessage.vote());
      log.info("Player voted: playerId={}, vote={}", sessionInfo.playerId(), voteMessage.vote());
    } catch (RuntimeException e) {
      log.error("Exception while voting, request={}: ", voteMessage, e);
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
      log.warn("Could not join the game: session={}, message={}, gameId={}",
        session.getRemoteAddress(), joinMessage, joinMessage.gameId());
    }
    log.info("Player joined the game: playerId={}, gameId={}", joinMessage.playerId(), joinMessage.gameId());
  }

  @Override
  // TODO I think I should rather create a some self-handling game instance for each game
  public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
    JoinMessage joinMessage = cleanLocalMaps(session);
    if (joinMessage == null) return;
    gameService.leaveGame(joinMessage.gameId(), joinMessage.playerId()); // TODO Maybe we should not leave, just mark as inactive?
    log.info("Player left the game: playerId={}, gameId={}", joinMessage.playerId(), joinMessage.gameId());
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

  @Override
  public void notify(@NonNull GameEvent gameEvent) {
    try {
      var payload = objectMapper.writeValueAsBytes(gameEvent);

      gameIdToParticipants.getOrDefault(gameEvent.gameId(), Collections.emptySet())
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
