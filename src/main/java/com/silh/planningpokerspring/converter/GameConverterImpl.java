package com.silh.planningpokerspring.converter;

import com.silh.planningpokerspring.domain.Game;
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

  private final PlayerConverter playerConverter;

  public GameConverterImpl(PlayerConverter playerConverter) {
    this.playerConverter = playerConverter;
  }

  @Override
  @Nullable
  public GameDto convert(@Nullable Game game) {
    if (game == null) {
      return null;
    }
    return new GameDto(
      game.getId(),
      game.getName(),
      playerConverter.convert(game.getCreator()),
      game.getState(),
      convertParticipants(game),
      game.getVotes()
    );
  }

  private Map<String, PlayerDto> convertParticipants(Game game) {
    return game.getPlayers()
      .entrySet()
      .stream()
      .collect(Collectors.toMap(Map.Entry::getKey, entry -> playerConverter.convert(entry.getValue())));
  }

}
