package com.silh.planningpokerspring.controller;

import com.silh.planningpokerspring.exception.UserNotFoundException;
import com.silh.planningpokerspring.request.GameDto;
import com.silh.planningpokerspring.request.NewGameRequest;
import com.silh.planningpokerspring.service.GameService;
import com.silh.planningpokerspring.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

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
    return ok().body(service.createGame(req));
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
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

  @ExceptionHandler(UserNotFoundException.class)
  public ErrorResponse handleUserNotFound(UserNotFoundException e) {
    return ErrorResponse.create(e, HttpStatus.NOT_FOUND, "User not found");
  }

}
