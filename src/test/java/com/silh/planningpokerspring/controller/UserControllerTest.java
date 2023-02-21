package com.silh.planningpokerspring.controller;

import com.silh.planningpokerspring.request.CreateUserRequest;
import com.silh.planningpokerspring.request.PlayerDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

  private final RestOperations restTemplate = new RestTemplate();
  @LocalServerPort
  private int randomServerPort;

  private String gameApiPath;

  @BeforeEach
  void setUp() {
    gameApiPath = "http://localhost:" + randomServerPort + "/api/users";
  }

  @Test
  void canCreateUser() {
    CreateUserRequest reqBody = new CreateUserRequest("hello");
    ResponseEntity<PlayerDto> responseEntity =
      restTemplate.postForEntity(gameApiPath, reqBody, PlayerDto.class);

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

    PlayerDto createdPlayer = responseEntity.getBody();
    assertThat(createdPlayer.name()).isEqualTo(reqBody.name());
    assertThat(createdPlayer.id()).isNotNull();
  }
}
