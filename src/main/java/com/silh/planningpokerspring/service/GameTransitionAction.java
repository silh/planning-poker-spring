package com.silh.planningpokerspring.service;

import com.silh.planningpokerspring.Game;

import java.util.function.Function;

public interface GameTransitionAction extends Function<Game, Boolean> {
}
