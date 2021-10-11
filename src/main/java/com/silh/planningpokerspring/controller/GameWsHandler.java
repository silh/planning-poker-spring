package com.silh.planningpokerspring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.silh.planningpokerspring.request.ws.WsMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Responsible for sending game state update events to the client.
 */
@Slf4j
@AllArgsConstructor
public class GameWsHandler extends TextWebSocketHandler {

  private final ObjectMapper objectMapper;

  private final Map<String, Set<WebSocketSession>> gameIdToParticipants = new HashMap<>();

  @Override
  public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
    log.debug("New connection from {}.", session.getRemoteAddress());
  }

  @Override
  protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
    final String payload = message.getPayload();
    final WsMessage<?> wsMessage = objectMapper.readValue(payload, WsMessage.class);
    switch (wsMessage) {
      case WsMessage.JoinMessage j -> System.out.println(j);
      case WsMessage.VoteMessage v -> System.out.println(v);
      default -> throw new IllegalStateException("Unexpected value: " + wsMessage); //How?
    }
    session.sendMessage(message);
  }
}
