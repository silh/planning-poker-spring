package com.silh.planningpokerspring.controller;

import com.silh.planningpokerspring.request.*;
import com.silh.planningpokerspring.service.GameService;
import com.silh.planningpokerspring.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

import static org.springframework.http.ResponseEntity.*;

@RequestMapping("/api/games")
@RestController
public class GameController {

  private final GameService service;

  public GameController(GameService service, UserService userService) {
    this.service = service;
  }

  @PostMapping(
    produces = MediaType.APPLICATION_JSON_VALUE,
    consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<GameDto> startGame(@RequestBody NewGameRequest req) {
    return ok().body(service.createGame(req.creatorId()));
  }

  @GetMapping(
    produces = MediaType.APPLICATION_JSON_VALUE,
    consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<List<GameDto>> getGames() {
    return ok().body(service.getGames());
  }

  @GetMapping(
    value = "/{id}",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<GameDto> getGame(@PathVariable("id") String gameId) {
    return service.getGame(gameId)
      .map(game -> ok().body(game))
      .orElseGet(() -> notFound().build());
  }

  @PostMapping(
    consumes = MediaType.APPLICATION_JSON_VALUE,
    value = "/{id}/advance"
  )
  public ResponseEntity<?> toNextState(@PathVariable("id") String gameId,
                                       @RequestBody TransitionRequest req,
                                       HttpSession session) {
    String personId = session.getId();
    boolean executed = service.transitionTo(gameId, personId, req.nextState());
    if (executed) {
      return accepted().build();
    }
    return badRequest().build();
  }

  @PostMapping(
    consumes = MediaType.APPLICATION_JSON_VALUE,
    value = "/{id}/join"
  )
  public ResponseEntity<?> join(@PathVariable("id") String gameId,
                                @RequestBody JoinRequest joinRequest) {
    final boolean joined = service.joinGame(gameId, joinRequest.playerId());
    if (joined) {
      return accepted().build();
    }
    return badRequest().build();
  }

  @PostMapping(
    consumes = MediaType.APPLICATION_JSON_VALUE,
    value = "/{id}/vote"
  )
  public ResponseEntity<?> vote(@PathVariable("id") String gameId,
                                @RequestBody VoteRequest request) {
    final boolean accepted = service.vote(gameId, request.playerId(), request.value());
    if (accepted) {
      return accepted().build();
    }
    return badRequest().build();
  }

}
