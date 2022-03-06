package com.silh.planningpokerspring.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.silh.planningpokerspring.controller.GameWsHandler;
import com.silh.planningpokerspring.converter.GameConverter;
import com.silh.planningpokerspring.converter.GameConverterImpl;
import com.silh.planningpokerspring.repository.ConcurrentHashMapGameRepository;
import com.silh.planningpokerspring.repository.GameRepository;
import com.silh.planningpokerspring.service.GameEventsSubscriber;
import com.silh.planningpokerspring.service.GameService;
import com.silh.planningpokerspring.service.GenericGameService;
import com.silh.planningpokerspring.service.NoOpGameEventSubscriber;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executors;

@AllArgsConstructor
@Configuration(proxyBeanMethods = false)
public class PokerConfig {

  private final ObjectMapper objectMapper;

  @Bean
  public GameRepository gameRepository() {
    return new ConcurrentHashMapGameRepository();
  }

  @Bean
  public GameConverter gameConverter() {
    return new GameConverterImpl();
  }

  @Bean
  public GameEventsSubscriber noOpGameEventSubscriber() {
    return new NoOpGameEventSubscriber();
  }

  @Bean
  public GameService gameService(List<GameEventsSubscriber> gameEventSubscribers) {
    return new GenericGameService(gameRepository(), gameConverter(), gameEventSubscribers);
  }

  @Bean
  public GameWsHandler gameWsHandler() {
    // TODO don't hardcode delay
    return new GameWsHandler(objectMapper, Executors.newSingleThreadScheduledExecutor(), Duration.ofSeconds(1));
  }
}
