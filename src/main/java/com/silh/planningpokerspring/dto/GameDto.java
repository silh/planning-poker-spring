package com.silh.planningpokerspring.dto;

import com.silh.planningpokerspring.Player;
import com.silh.planningpokerspring.RoundState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameDto {
  private String id;
  private Player creator;
  private RoundState state;
  private Map<String, Player> participants;
  private Map<String, Long> votes;
}
