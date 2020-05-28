package com.silh.planningpokerspring.config;

import com.silh.planningpokerspring.converter.GameConverter;
import com.silh.planningpokerspring.converter.GameConverterImpl;
import com.silh.planningpokerspring.repository.ConcurrentHashMapGameRepository;
import com.silh.planningpokerspring.repository.GameRepository;
import com.silh.planningpokerspring.service.GameEventSubscriber;
import com.silh.planningpokerspring.service.GameService;
import com.silh.planningpokerspring.service.GenericGameService;
import com.silh.planningpokerspring.service.NoOpGameEventSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class PokerConfig {

  @Bean
  public GameRepository gameRepository() {
    return new ConcurrentHashMapGameRepository();
  }

  @Bean
  public GameConverter gameConverter() {
    return new GameConverterImpl();
  }

  @Bean
  public GameEventSubscriber noOpGameEventSubscriber() {
    return new NoOpGameEventSubscriber();
  }

  @Bean
  public GameService gameService(List<GameEventSubscriber> gameEventSubscribers) {
    return new GenericGameService(gameRepository(), gameConverter(), gameEventSubscribers);
  }
}
