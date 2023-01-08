package com.silh.planningpokerspring.controller;

import com.silh.planningpokerspring.domain.Player;
import com.silh.planningpokerspring.request.*;
import com.silh.planningpokerspring.service.GameService;
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

  public GameController(GameService service) {
    this.service = service;
  }

  @PostMapping(
    produces = MediaType.APPLICATION_JSON_VALUE,
    consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<GameDto> startGame(@RequestBody NewGameRequest req, HttpSession session) {
    Player creator = new Player(session.getId(), req.name());
    return ok().body(service.createGame(creator));
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
                                @RequestBody JoinRequest joinRequest,
                                HttpSession session) {
    final boolean joined = service.joinGame(gameId, new Player(session.getId(), joinRequest.name()));
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
                                @RequestBody VoteRequest request,
                                HttpSession session) {
    final boolean accepted = service.vote(gameId, session.getId(), request.value());
    if (accepted) {
      return accepted().build();
    }
    return badRequest().build();
  }

}
