package com.silh.planningpokerspring.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.silh.planningpokerspring.controller.GameWsHandler;
import com.silh.planningpokerspring.converter.GameConverter;
import com.silh.planningpokerspring.converter.GameConverterImpl;
import com.silh.planningpokerspring.converter.PlayerConverter;
import com.silh.planningpokerspring.repository.GameRepository;
import com.silh.planningpokerspring.repository.HashMapGameRepository;
import com.silh.planningpokerspring.repository.UserRepository;
import com.silh.planningpokerspring.service.*;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.Executors;

@AllArgsConstructor
@Configuration(proxyBeanMethods = false)
public class PokerConfig {

  private final ObjectMapper objectMapper;

  @Bean
  public StringIdGenerator stringIdGenerator() {
    return new StringIdGenerator();
  }

  @Bean
  public UserService userService(PlayerConverter playerConverter, UserRepository userRepository) {
    return new UserService(userRepository, playerConverter);
  }

  @Bean
  public UserRepository userRepository(StringIdGenerator idGenerator) {
    return new UserRepository(idGenerator);
  }

  @Bean
  public PlayerConverter playerConverter() {
    return new PlayerConverter();
  }

  @Bean
  public GameConverter gameConverter(PlayerConverter playerConverter) {
    return new GameConverterImpl(playerConverter);
  }

  @Bean
  public GameEventsSubscriber noOpGameEventSubscriber() {
    return new NoOpGameEventSubscriber();
  }

  @Bean
  public GameRepository gameRepository(StringIdGenerator idGenerator) {
    return new HashMapGameRepository(idGenerator);
  }

  @Bean
  public GameService gameService(
    GameRepository gameRepository,
    UserRepository userRepository,
    GameConverter gameConverter
  ) {
    return new GenericGameService(gameRepository, userRepository, gameConverter);
  }

  @Bean
  public GameWsHandler gameWsHandler(GameService gameService) {
    // TODO don't hardcode delay
    return new GameWsHandler(
      objectMapper,
      gameService,
      Executors.newSingleThreadScheduledExecutor(),
      Duration.ofSeconds(1)
    );
  }
}
