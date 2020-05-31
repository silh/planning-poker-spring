package com.silh.planningpokerspring.converter;

import com.silh.planningpokerspring.domain.Game;
import com.silh.planningpokerspring.request.GameDto;
import org.springframework.core.convert.converter.Converter;

public interface GameConverter extends Converter<Game, GameDto> {
}
