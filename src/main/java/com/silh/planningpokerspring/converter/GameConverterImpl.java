package com.silh.planningpokerspring.converter;

import com.silh.planningpokerspring.Game;
import com.silh.planningpokerspring.dto.GameDto;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

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
    gameDto.setCreator(game.getCreator());
    gameDto.setState(game.getState());
    gameDto.setParticipants(game.getParticipants());
    gameDto.setVotes(game.getVotes());

    return gameDto;
  }
}
