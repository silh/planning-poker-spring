package com.silh.planningpokerspring.controller;

import com.silh.planningpokerspring.request.GameDto;
import com.silh.planningpokerspring.request.NewGameRequest;
import com.silh.planningpokerspring.request.PlayerDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameControllerTest {

  private final RestOperations restTemplate = new RestTemplate();
  @LocalServerPort
  private int randomServerPort;
  private String baseUri;

  private String gameApiPath;

  @BeforeEach
  void setUp() {
    baseUri = "http://localhost:" + randomServerPort;
    gameApiPath = baseUri + "/api/game";
  }

  @Test
  void canStartGame() {
    //Create a game
    final NewGameRequest newGameRequest = new NewGameRequest("harry");
    final ResponseEntity<GameDto> response = restTemplate.postForEntity(gameApiPath, newGameRequest, GameDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    //Check returned body
    final GameDto game = response.getBody();
    assertThat(game).isNotNull();
    assertThat(game.getId())
      .isNotNull()
      .isNotEmpty();
    final PlayerDto creator = game.getCreator();
    assertThat(creator).isNotNull();
    assertThat(creator.getName()).isEqualTo(newGameRequest.getCreatorName());
    final HttpHeaders httpHeaders = getHttpHeaders(response);

    //Check get
    String getGamePath = gameApiPath + "/" + game.getId();
    final ResponseEntity<GameDto> getGameResponse =
      restTemplate.exchange(getGamePath, HttpMethod.GET, new HttpEntity<>(httpHeaders), GameDto.class);
    assertThat(getGameResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    final GameDto gotGame = getGameResponse.getBody();
    assertThat(gotGame).isEqualTo(game);
  }

  private HttpHeaders getHttpHeaders(ResponseEntity<?> response) {
    // From now on we need JSESSIONID
    final List<String> cookies = response.getHeaders().get("Set-Cookie");
    assertThat(cookies)
      .isNotNull()
      .isNotEmpty();

    final String sessionCookie = cookies.get(0);
    final HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Cookie", sessionCookie);
    return httpHeaders;
  }
}
