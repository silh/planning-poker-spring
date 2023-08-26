package com.silh.planningpokerspring.request;

import com.silh.planningpokerspring.domain.GameState;

import java.util.Map;

/**
 * Represents game state returned as a response.
 */
public record GameDto(
  String id,
  String name,
  PlayerDto creator,
  GameState state,
  Map<String, PlayerDto> players,
  Map<String, String> votes
) {
}
