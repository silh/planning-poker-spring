package com.silh.planningpokerspring.converter;

import com.silh.planningpokerspring.domain.RoundResult;
import com.silh.planningpokerspring.request.RoundResultDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

import java.util.Map;
import java.util.stream.Collectors;

public class RoundResultConverter implements Converter<RoundResult, RoundResultDto> {

  private final PlayerConverter playerConverter;

  public RoundResultConverter(PlayerConverter playerConverter) {
    this.playerConverter = playerConverter;
  }

  @Override
  public RoundResultDto convert(@NonNull RoundResult source) {
    final var convertedPlayers = source.players()
      .entrySet()
      .stream()
      .collect(Collectors.toMap(Map.Entry::getKey, e -> playerConverter.convert(e.getValue())));
    return new RoundResultDto(convertedPlayers, source.votes());
  }
}
