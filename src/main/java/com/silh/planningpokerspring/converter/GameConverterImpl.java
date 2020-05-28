package com.silh.planningpokerspring.converter;

import com.silh.planningpokerspring.Game;
import com.silh.planningpokerspring.Player;
import com.silh.planningpokerspring.request.GameDto;
import com.silh.planningpokerspring.request.PlayerDto;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Converter from {@link Game} domain model to {@link GameDto}.
 */
@Component
public class GameConverterImpl implements GameConverter {

  @Override
  @Nullable
  public GameDto convert(@Nullable Game game) {
    if (game == null) {
      return null;
    }
    final GameDto gameDto = new GameDto();
    gameDto.setId(game.getId());
    gameDto.setCreator(toPlayerDto(game.getCreator()));
    gameDto.setState(game.getState());
    final Map<String, PlayerDto> participants = convertParticipants(game);
    gameDto.setParticipants(participants);
    gameDto.setVotes(game.getVotes());

    return gameDto;
  }

  private static Map<String, PlayerDto> convertParticipants(Game game) {
    return game.getParticipants()
      .entrySet()
      .stream()
      .collect(Collectors.toMap(Map.Entry::getKey, entry -> toPlayerDto(entry.getValue())));
  }

  private static PlayerDto toPlayerDto(Player player) {
    return new PlayerDto(player.getName());
  }
}
