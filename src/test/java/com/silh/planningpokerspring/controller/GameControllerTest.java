package com.silh.planningpokerspring.controller;

import com.silh.planningpokerspring.Game;
import com.silh.planningpokerspring.Player;
import com.silh.planningpokerspring.RoundState;
import com.silh.planningpokerspring.dto.NewGameRequest;
import com.silh.planningpokerspring.dto.TransitionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameControllerTest {

  private final TestRestTemplate restTemplate = new TestRestTemplate();
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
    final ResponseEntity<Game> response = restTemplate.postForEntity(gameApiPath, newGameRequest, Game.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    //Check returned body
    final Game game = response.getBody();
    assertThat(game).isNotNull();
    assertThat(game.getId())
      .isNotNull()
      .isNotEmpty();
    final Player creator = game.getCreator();
    assertThat(creator).isNotNull();
    assertThat(creator.getName()).isEqualTo(newGameRequest.getCreatorName());
    final HttpHeaders httpHeaders = getHttpHeaders(response);

    //Check get
    String getGamePath = gameApiPath + "/" + game.getId();
    final ResponseEntity<Game> getGameResponse =
      restTemplate.exchange(getGamePath, HttpMethod.GET, new HttpEntity<>(httpHeaders), Game.class);
    assertThat(getGameResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    final Game gotGame = getGameResponse.getBody();
    assertThat(gotGame).isEqualTo(game);
    //FIXME
  }

  @Test
  void canStartRound() {
    //Create a game
    final NewGameRequest newGameRequest = new NewGameRequest("harry");
    final ResponseEntity<Game> response = restTemplate.postForEntity(gameApiPath, newGameRequest, Game.class);
    final Game game = response.getBody();
    assertThat(game).isNotNull();

    //Start it
    final HttpHeaders httpHeaders = getHttpHeaders(response);
    final String gameTransitionPath = gameApiPath + "/" + game.getId() + "/advance";
    final TransitionRequest transitionRequest = new TransitionRequest(RoundState.VOTING);
    final ResponseEntity<Object> updated =
      restTemplate.postForEntity(gameTransitionPath, new HttpEntity<>(transitionRequest, httpHeaders), Object.class);
    assertThat(updated.getStatusCode())
      .isEqualTo(HttpStatus.ACCEPTED);
    //FIXME
  }

  private HttpHeaders getHttpHeaders(ResponseEntity<Game> response) {
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
