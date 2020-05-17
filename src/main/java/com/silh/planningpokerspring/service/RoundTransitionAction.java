package com.silh.planningpokerspring.service;

import com.silh.planningpokerspring.Round;

import java.util.function.Function;

public interface RoundTransitionAction extends Function<Round, Boolean> {
}
