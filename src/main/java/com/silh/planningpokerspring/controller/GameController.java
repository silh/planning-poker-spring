package com.silh.planningpokerspring.controller;

import com.silh.planningpokerspring.Player;
import com.silh.planningpokerspring.request.GameDto;
import com.silh.planningpokerspring.request.NewGameRequest;
import com.silh.planningpokerspring.request.TransitionRequest;
import com.silh.planningpokerspring.request.VoteRequest;
import com.silh.planningpokerspring.service.GameService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

import static org.springframework.http.ResponseEntity.*;

@RequestMapping("/api/game")
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
    Player creator = new Player(session.getId(), req.getCreatorName());
    return ok().body(service.createGame(creator));
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
    boolean executed = service.transitionTo(gameId, session.getId(), req.getNextState());
    if (executed) {
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
    final boolean accepted = service.vote(gameId, session.getId(), request.getValue());
    if (accepted) {
      return accepted().build();
    }
    return badRequest().build();
  }
}
