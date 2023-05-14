package com.silh.planningpokerspring.service;

import com.silh.planningpokerspring.converter.PlayerConverter;
import com.silh.planningpokerspring.repository.UserRepository;
import com.silh.planningpokerspring.request.PlayerDto;
import org.springframework.lang.NonNull;

import java.util.Optional;

public class UserService {

  private final UserRepository userRepository;
  private final PlayerConverter playerConverter;

  public UserService(UserRepository userRepository, PlayerConverter playerConverter) {
    this.userRepository = userRepository;
    this.playerConverter = playerConverter;
  }

  @NonNull
  public PlayerDto create(String name) {
    return playerConverter.convert(
      userRepository.create(name)
    );
  }

  public Optional<PlayerDto> get(String id) {
    return userRepository.find(id)
      .map(playerConverter::convert);
  }
}
