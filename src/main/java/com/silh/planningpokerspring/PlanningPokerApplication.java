package com.silh.planningpokerspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
  "com.silh.planningpokerspring.config",
  "com.silh.planningpokerspring.controller"
})
public class PlanningPokerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlanningPokerApplication.class, args);
	}

}
