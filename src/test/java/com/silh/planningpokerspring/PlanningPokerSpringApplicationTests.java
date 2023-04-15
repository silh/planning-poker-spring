package com.silh.planningpokerspring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.silh.planningpokerspring.request.*;
import com.silh.planningpokerspring.request.ws.JoinMessage;
import com.silh.planningpokerspring.service.events.GameEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PlanningPokerSpringApplicationTests {

  private final WebSocketClient wsClient = new StandardWebSocketClient();

  private final RestOperations restTemplate = new RestTemplate();
  private final ObjectMapper objectMapper = new ObjectMapper();
  @LocalServerPort
  private int randomServerPort;
  private String userApiPath;
  private String gameApiPath;
  private String wsPath;

  @BeforeEach
  void setUp() {
    final var serverPath = "http://localhost:" + randomServerPort;
    userApiPath = serverPath + "/api/users";
    gameApiPath = serverPath + "/api/games";
    wsPath = "ws://localhost:" + randomServerPort + "/ws";
  }

  @Test
  void contextLoads() {
  }

  @Test
  void createJoinStartVoteEnd() throws InterruptedException, IOException, ExecutionException {
    //Create users
    final PlayerDto creator = createUser("bobby");
    final PlayerDto joiner = createUser("joiner");

    //Create a game
    final NewGameRequest newGameRequest = new NewGameRequest("harry", creator.id());
    final ResponseEntity<GameDto> response = restTemplate.postForEntity(gameApiPath, newGameRequest, GameDto.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    //Check returned body
    final GameDto ongoingGame = response.getBody();
    assertThat(ongoingGame).isNotNull();
    assertThat(ongoingGame.id())
      .isNotNull()
      .isNotEmpty();
    assertThat(ongoingGame.participants()).isEmpty();
    assertThat(ongoingGame.votes()).isEmpty();
    assertThat(creator).isNotNull();
    assertThat(creator.id()).isEqualTo(newGameRequest.creatorId());

    //Check game
    String getGamePath = gameApiPath + "/" + ongoingGame.id();
    final ResponseEntity<GameDto> getGameResponse = restTemplate.getForEntity(getGamePath, GameDto.class);
    assertThat(getGameResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(getGameResponse.getBody()).isEqualTo(ongoingGame);

    // Join
    var wsHandler = new SyncWebsocketHandler();
    wsClient.doHandshake(wsHandler, wsPath);
    GameEvent gameEvent = wsHandler.join(new JoinMessage(ongoingGame.id(), joiner.id()));
    ongoingGame.participants().put(joiner.id(), joiner);
//    assertThat(gameEvent).isEqualTo(ongoingGame);

    //Participant can vote
    final long voteValue = 1L;
    final VoteRequest voteRequest = new VoteRequest(joiner.id(), voteValue);
    final ResponseEntity<Object> votedResponse = restTemplate.postForEntity(gameApiPath + "/" + ongoingGame.id() + "/vote", voteRequest, Object.class
    );
    assertThat(votedResponse.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

    //Check game
    // FIXME should not be able to do that until the game is in the correct state
    ongoingGame.votes().put(joiner.id(), voteValue);
    final ResponseEntity<GameDto> votedGame =
      restTemplate.getForEntity(getGamePath, GameDto.class);
    assertThat(votedGame.getBody()).isEqualTo(ongoingGame);
  }

  private PlayerDto createUser(String name) {
    CreateUserRequest createUserRequest = new CreateUserRequest(name);
    ResponseEntity<PlayerDto> createdPlayerResp = restTemplate.postForEntity(userApiPath, createUserRequest, PlayerDto.class);
    assertThat(createdPlayerResp.getStatusCode()).isEqualTo(HttpStatus.OK);
    final PlayerDto creator = createdPlayerResp.getBody();
    assertThat(creator).isNotNull();
    return creator;
  }

  class SyncWebsocketHandler extends TextWebSocketHandler {

    private final CompletableFuture<WebSocketSession> session = new CompletableFuture<>();
    private final ArrayBlockingQueue<GameEvent> q = new ArrayBlockingQueue<>(1);

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
      this.session.complete(session);
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) throws Exception {
      q.offer(objectMapper.readValue(message.getPayload(), GameEvent.class));
    }

    public GameEvent join(JoinMessage joinMessage) throws IOException, ExecutionException, InterruptedException {
      this.session.get().sendMessage(new TextMessage(objectMapper.writeValueAsString(joinMessage)));
      GameEvent gameEvent = q.poll(100, TimeUnit.SECONDS);
      assertThat(gameEvent).isNotNull();
      return gameEvent;
    }
  }

}
