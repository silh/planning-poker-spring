package com.silh.planningpokerspring.controller;

import com.silh.planningpokerspring.domain.GameState;
import com.silh.planningpokerspring.request.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameControllerTest {

  private static final Pattern SESSION_ID_PATTERN = Pattern.compile("(?<=JSESSIONID=)\\w*");
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
  void createJoinStartVoteEnd() {
    //Create a game
    final NewGameRequest newGameRequest = new NewGameRequest("harry");
    final ResponseEntity<GameDto> response = restTemplate.postForEntity(gameApiPath, newGameRequest, GameDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    //Check returned body
    final GameDto initialGame = response.getBody();
    assertThat(initialGame).isNotNull();
    assertThat(initialGame.getId())
      .isNotNull()
      .isNotEmpty();
    final PlayerDto creator = initialGame.getCreator();
    assertThat(creator).isNotNull();
    assertThat(creator.getName()).isEqualTo(newGameRequest.getName());
    final HttpHeaders creatorHeaders = getHttpHeaders(response);

    //Check game
    String getGamePath = gameApiPath + "/" + initialGame.getId();
    final ResponseEntity<GameDto> getGameResponse =
      restTemplate.exchange(getGamePath, HttpMethod.GET, new HttpEntity<>(creatorHeaders), GameDto.class);
    assertThat(getGameResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(getGameResponse.getBody()).isEqualTo(initialGame);

    //Start
    final TransitionRequest start = new TransitionRequest(GameState.VOTING);
    final ResponseEntity<Object> startedResp = restTemplate.postForEntity(
      gameApiPath + "/" + initialGame.getId() + "/advance",
      new HttpEntity<>(start, creatorHeaders),
      Object.class
    );
    assertThat(startedResp.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
    //Check game
    initialGame.setState(GameState.VOTING);
    final ResponseEntity<GameDto> votingGameResponse =
      restTemplate.exchange(getGamePath, HttpMethod.GET, new HttpEntity<>(creatorHeaders), GameDto.class);
    assertThat(votingGameResponse.getBody()).isEqualTo(initialGame);

    // Join
    final String playerName = "joiner";
    final JoinRequest joinRequest = new JoinRequest(playerName);
    final ResponseEntity<Object> joinedGameResp = restTemplate.postForEntity(
      gameApiPath + "/" + initialGame.getId() + "/join",
      joinRequest,
      Object.class
    );
    assertThat(joinedGameResp.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
    final HttpHeaders playerHeaders = getHttpHeaders(joinedGameResp);

    //Check game
    final String playerSessionId = getSessionId(joinedGameResp);
    initialGame.getParticipants().put(playerSessionId, new PlayerDto(playerName));
    final ResponseEntity<GameDto> joinedGameCheckResp =
      restTemplate.exchange(getGamePath, HttpMethod.GET, new HttpEntity<>(playerHeaders), GameDto.class);
    assertThat(joinedGameCheckResp.getBody()).isEqualTo(initialGame);

    //Participant can vote
    final long voteValue = 1L;
    final VoteRequest voteRequest = new VoteRequest(voteValue);
    final ResponseEntity<Object> votedResponse = restTemplate.postForEntity(
      gameApiPath + "/" + initialGame.getId() + "/vote",
      new HttpEntity<>(voteRequest, playerHeaders),
      Object.class
    );
    assertThat(votedResponse.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    //Check game
    initialGame.getVotes().put(playerSessionId, voteValue);
    final ResponseEntity<GameDto> votedGame =
      restTemplate.exchange(getGamePath, HttpMethod.GET, new HttpEntity<>(playerHeaders), GameDto.class);
    assertThat(votedGame.getBody()).isEqualTo(initialGame);
  }

  private static HttpHeaders getHttpHeaders(ResponseEntity<?> response) {
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

  private static String getSessionId(ResponseEntity<?> response) {
    final String sessionHeader = Objects.requireNonNull(
      response.getHeaders()
        .get("Set-Cookie")).get(0);
    final Matcher matcher = SESSION_ID_PATTERN.matcher(sessionHeader);
    if (!matcher.find()) {
      throw new RuntimeException("Incorrect session header: " + sessionHeader);
    }
    return matcher.group();
  }
}
