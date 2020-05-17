package com.silh.planningpokerspring.controller;

import com.silh.planningpokerspring.Game;
import com.silh.planningpokerspring.Player;
import com.silh.planningpokerspring.dto.NewGameRequest;
import com.silh.planningpokerspring.dto.TransitionRequest;
import com.silh.planningpokerspring.dto.VoteRequest;
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
  public ResponseEntity<Game> startGame(@RequestBody NewGameRequest req, HttpSession session) {
    Player creator = new Player(session.getId(), req.getCreatorName());
    return ok().body(service.createGame(creator));
  }

  @GetMapping(
    value = "/{id}",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<Game> getGame(@PathVariable("id") String gameId) {
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
    boolean executed;
    switch (req.getNextState()) {
      case VOTING:
        executed = service.startRound(gameId, session.getId());
        break;
      case DISCUSSION:
        executed = false;
        throw new IllegalArgumentException("not implemented");
      default:
        throw new IllegalArgumentException("not implemented");
    }
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
