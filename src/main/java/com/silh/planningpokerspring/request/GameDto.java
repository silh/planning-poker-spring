package com.silh.planningpokerspring.request;

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
  private PlayerDto creator;
  private RoundState state;
  private Map<String, PlayerDto> participants;
  private Map<String, Long> votes;
}
