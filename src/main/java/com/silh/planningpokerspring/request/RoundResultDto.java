package com.silh.planningpokerspring.request;

import java.util.Map;

public record RoundResultDto(Map<String, PlayerDto> players, Map<String, String> votes) {
}
