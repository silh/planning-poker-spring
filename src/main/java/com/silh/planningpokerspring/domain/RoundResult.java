package com.silh.planningpokerspring.domain;

import java.util.Map;

public record RoundResult(Map<String, Player> players, Map<String, String> votes) {
}
