package com.silh.planningpokerspring.service;

import org.hashids.Hashids;

import java.util.concurrent.ThreadLocalRandom;

public class StringIdGenerator {

  private final Hashids idGenerator = new Hashids("add some salt, per favore");

  public String generate() {
    final ThreadLocalRandom random = ThreadLocalRandom.current();
    return idGenerator.encode(random.nextLong(Hashids.MAX_NUMBER),
      random.nextLong(Hashids.MAX_NUMBER),
      random.nextLong(Hashids.MAX_NUMBER));
  }
}
