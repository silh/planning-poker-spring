package com.silh.planningpokerspring.service;

import com.silh.planningpokerspring.repository.GameRepository;
import org.springframework.stereotype.Service;

@Service
public class GameService {

  private final GameRepository repository;

  public GameService(GameRepository repository) {
    this.repository = repository;
  }


}
