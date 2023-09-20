package com.silh.planningpokerspring.converter;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

class GameConverterImplTest {


  @Test
  void override() {
    SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
    Tags tags = Tags.of("currency", "a");
    Gauge.builder("withdraw.cycle", () -> 10)
      .tags(tags)
      .strongReference(true)
      .register(meterRegistry);
    System.out.println(meterRegistry.getMetersAsString());
    Gauge.builder("withdraw.cycle", () -> 20)
      .tags(tags)
      .strongReference(true)
      .register(meterRegistry);
    System.out.println(meterRegistry.getMetersAsString());
  }

  @Test
  void atomic() {
    ConcurrentMap<String, AtomicLong> cycleGaugeMap = new ConcurrentHashMap<>();
    SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
    String currency = "a";
    Consumer<Integer> setMinutes = (minutes) -> cycleGaugeMap.compute(currency, (k, v) -> {
      if (v == null) {
        v = meterRegistry.gauge("withdraw.cycle", Tags.of("currency", currency), new AtomicLong(minutes));
      } else {
        v.set(minutes);
      }
      return v;
    });
    setMinutes.accept(10);
    System.out.println(meterRegistry.getMetersAsString());
    setMinutes.accept(20);
    System.out.println(meterRegistry.getMetersAsString());
  }
}
