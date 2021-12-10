package com.silh.planningpokerspring.request;

import com.silh.planningpokerspring.domain.GameState;

import java.util.Map;

/**
 * Represents game state returned as a response.
 */
public record GameDto(
  String id,
  PlayerDto creator,
  GameState state,
  Map<String, PlayerDto> participants,
  Map<String, Long> votes
) {
}
