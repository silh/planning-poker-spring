package com.silh.planningpokerspring.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerDto {
  private String name; //FIXME need player ID to be able to to match player and vote.
}
