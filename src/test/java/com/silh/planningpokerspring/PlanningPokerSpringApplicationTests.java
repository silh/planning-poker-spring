package com.silh.planningpokerspring;

import com.silh.planningpokerspring.request.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PlanningPokerSpringApplicationTests {

  private final RestOperations restTemplate = new RestTemplate();
  @LocalServerPort
  private int randomServerPort;
  private String userApiPath;
  private String gameApiPath;

  @BeforeEach
  void setUp() {
    final var serverPath = "http://localhost:" + randomServerPort;
    userApiPath = serverPath + "/api/users";
    gameApiPath = serverPath + "/api/games";
  }

  @Test
  void contextLoads() {
  }

  @Test
  void createJoinStartVoteEnd() {
    //Create users
    final PlayerDto creator = createUser("bobby");
    final PlayerDto joiner = createUser("joiner");

    //Create a game
    final NewGameRequest newGameRequest = new NewGameRequest("harry", creator.id());
    final ResponseEntity<GameDto> response = restTemplate.postForEntity(gameApiPath, newGameRequest, GameDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    //Check returned body
    final GameDto initialGame = response.getBody();
    var expected = initialGame; // at first, they are equal
    assertThat(initialGame).isNotNull();
    assertThat(initialGame.id())
      .isNotNull()
      .isNotEmpty();
    assertThat(creator).isNotNull();
    assertThat(creator.id()).isEqualTo(newGameRequest.creatorId());

    //Check game
    String getGamePath = gameApiPath + "/" + expected.id();
    final ResponseEntity<GameDto> getGameResponse =
      restTemplate.getForEntity(getGamePath, GameDto.class);
    assertThat(getGameResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(getGameResponse.getBody()).isEqualTo(expected);

    //Start
//    // FIXME we won't have this call at all soon.
//    final ResponseEntity<Object> startedResp = restTemplate.postForEntity(
//      gameApiPath + "/" + expected.id() + "/advance",
//      new TransitionRequest(GameState.VOTING),
//      Object.class
//    );
//    assertThat(startedResp.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
    //Check game
//    expected = new GameDto(
//      expected.id(),
//      expected.creator(),
//      GameState.VOTING,
//      expected.participants(),
//      expected.votes()
//    );
    final ResponseEntity<GameDto> votingGameResponse =
      restTemplate.getForEntity(getGamePath, GameDto.class);
    assertThat(votingGameResponse.getBody()).isEqualTo(expected);

    // Join
    final JoinRequest joinRequest = new JoinRequest(joiner.id());
    final ResponseEntity<PlayerDto> joinedGameResp = restTemplate.postForEntity(
      gameApiPath + "/" + expected.id() + "/join",
      joinRequest,
      PlayerDto.class
    );
    assertThat(joinedGameResp.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    //Check game
    expected.participants().put(joiner.id(), joiner);
    final ResponseEntity<GameDto> joinedGameCheckResp = restTemplate.getForEntity(getGamePath, GameDto.class);
    assertThat(joinedGameCheckResp.getBody()).isEqualTo(expected);

    //Participant can vote
    final long voteValue = 1L;
    final VoteRequest voteRequest = new VoteRequest(joiner.id(), voteValue);
    final ResponseEntity<Object> votedResponse = restTemplate.postForEntity(
      gameApiPath + "/" + initialGame.id() + "/vote",
      voteRequest,
      Object.class
    );
    assertThat(votedResponse.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    //Check game
    // FIXME should not be able to do that until the game is in the correct state
    initialGame.votes().put(joiner.id(), voteValue);
    final ResponseEntity<GameDto> votedGame =
      restTemplate.getForEntity(getGamePath, GameDto.class);
    assertThat(votedGame.getBody()).isEqualTo(expected);
  }

  private PlayerDto createUser(String name) {
    CreateUserRequest createUserRequest = new CreateUserRequest(name);
    ResponseEntity<PlayerDto> createdPlayerResp =
      restTemplate.postForEntity(userApiPath, createUserRequest, PlayerDto.class);
    assertThat(createdPlayerResp.getStatusCode()).isEqualTo(HttpStatus.OK);
    final PlayerDto creator = createdPlayerResp.getBody();
    assertThat(creator).isNotNull();
    return creator;
  }

}
