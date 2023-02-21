package com.silh.planningpokerspring.converter;

import com.silh.planningpokerspring.domain.Player;
import com.silh.planningpokerspring.request.PlayerDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class PlayerConverter implements Converter<Player, PlayerDto> {

  @Override
  @NonNull
  public PlayerDto convert(@Nullable Player source) {
    if (source == null) {
      throw new NullPointerException();
    }
    return new PlayerDto(source.id(), source.name());
  }
}
