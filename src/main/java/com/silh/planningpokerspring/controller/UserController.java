package com.silh.planningpokerspring.controller;

import com.silh.planningpokerspring.request.CreateUserRequest;
import com.silh.planningpokerspring.request.PlayerDto;
import com.silh.planningpokerspring.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

@RequestMapping("/api/users")
@RestController
public class UserController {

  public final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping(
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<PlayerDto> createPlayer(@RequestBody CreateUserRequest req) {
    return ok().body(userService.create(req.name()));
  }

  @GetMapping(
    value = "/{id}",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<PlayerDto> getPlayer(@PathVariable("id") String id) {
    return userService.get(id)
      .map(p -> ok().body(p))
      .orElse(notFound().build());
  }
}
