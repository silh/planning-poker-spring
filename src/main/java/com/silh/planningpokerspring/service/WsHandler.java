package com.silh.planningpokerspring.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
public class WsHandler extends TextWebSocketHandler {

  @Override
  public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
    log.info("new connection");
  }

  @Override
  protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
    session.sendMessage(message);
  }
}
