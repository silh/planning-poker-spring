package com.silh.planningpokerspring.service;

import com.silh.planningpokerspring.converter.GameConverterImpl;
import com.silh.planningpokerspring.converter.PlayerConverter;
import com.silh.planningpokerspring.domain.Game;
import com.silh.planningpokerspring.domain.GameState;
import com.silh.planningpokerspring.domain.Player;
import com.silh.planningpokerspring.repository.GameRepository;
import com.silh.planningpokerspring.repository.UserRepository;
import com.silh.planningpokerspring.request.GameDto;
import com.silh.planningpokerspring.request.PlayerDto;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GenericGameServiceTest {
  private final GameRepository mockGameRepository = mock(GameRepository.class);
  private final UserRepository mockUserRepository = mock(UserRepository.class);
  private final PlayerConverter playerConverter = new PlayerConverter();
  private final GenericGameService gameService =
    new GenericGameService(mockGameRepository, mockUserRepository, new GameConverterImpl(playerConverter));

  @Test
  void canCreateGame() {
    final Player creator = new Player("1", "2");
    final Game createdGame = new Game("id", creator);
    when(mockGameRepository.create(creator)).thenReturn(createdGame);
    when(mockUserRepository.find(creator.id())).thenReturn(Optional.of(creator));

    final GameDto result = gameService.createGame(creator.id());
    isGameDtoEqualToGame(result, createdGame);
  }

  @Test
  void canGetAGame() {
    final Player creator = new Player("1", "2");
    final Game expectedGame = new Game("id", creator);
    when(mockGameRepository.find(expectedGame.getId())).thenReturn(Optional.of(expectedGame));

    final Optional<GameDto> result = gameService.getGame(expectedGame.getId());
    assertThat(result).isPresent()
      .hasValueSatisfying(dto -> isGameDtoEqualToGame(dto, expectedGame));
  }

  @Test
  void getGameReturnEmptyOptionIfGameDoesntExist() {
    final String gameId = "1";
    when(mockGameRepository.find(gameId)).thenReturn(Optional.empty());

    final Optional<GameDto> result = gameService.getGame(gameId);
    assertThat(result).isNotPresent();
  }

  @Test
  void canJoinGame() {
    final Player creator = new Player("1", "1");
    final Game expectedGame = new Game("id", creator);
    final Player player = new Player("2", "2");
    when(mockGameRepository.find(expectedGame.getId())).thenReturn(Optional.of(expectedGame));
    when(mockUserRepository.find(player.id())).thenReturn(Optional.of(player));

    final boolean joined = gameService.joinGame(expectedGame.getId(), player.id());
    assertThat(joined).isTrue();
    assertThat(expectedGame.getParticipants()).containsEntry("2", player);
  }

  @Test
  void cantJoinGameIfItDoesntExist() {
    final String gameId = "id";
    final Player player = new Player("2", "2");
    when(mockGameRepository.find(gameId)).thenReturn(Optional.empty());
    when(mockUserRepository.find(player.id())).thenReturn(Optional.of(player));

    final boolean joined = gameService.joinGame(gameId, player.id());
    assertThat(joined).isFalse();
  }

  @Test
  void creatorCanTransitionGameToAnotherStage() {
    final Player creator = new Player("1", "1");
    final Game expectedGame = new Game("id", creator);
    when(mockGameRepository.findByIdAndOwnerId(expectedGame.getId(), creator.id()))
      .thenReturn(Optional.of(expectedGame));

    final GameState nextState = GameState.VOTING;
    final boolean transitioned =
      gameService.transitionTo(expectedGame.getId(), creator.id(), nextState);
    assertThat(transitioned).isTrue();
    assertThat(expectedGame.getState()).isEqualTo(nextState);
  }

  @Test
  void canVote() {
    final Player creator = new Player("1", "1");
    final Game expectedGame = new Game("id", creator);
    final Player voter = new Player("2", "voter");
    expectedGame.addParticipant(voter);
    when(mockGameRepository.find(expectedGame.getId()))
      .thenReturn(Optional.of(expectedGame));

    final boolean updated = gameService.vote(expectedGame.getId(), voter.id(), 1L);
    assertThat(updated).isTrue();
    assertThat(expectedGame.getVotes()).containsEntry(voter.id(), 1L);
  }

  @Test
  void cantVoteIfNotParticipant() {
    final Player creator = new Player("1", "1");
    final Game expectedGame = new Game("id", creator);
    final Player voter = new Player("2", "voter");
    when(mockGameRepository.find(expectedGame.getId()))
      .thenReturn(Optional.of(expectedGame));

    final boolean updated = gameService.vote(expectedGame.getId(), voter.id(), 1L);
    assertThat(updated).isFalse();
    assertThat(expectedGame.getVotes()).doesNotContainKey(voter.id());
  }

  @Test
  void canGetGames() {
    final Player creator = new Player("1", "1");
    final Game expectedGame = new Game("id", creator);
    when((mockGameRepository.findAll())).thenReturn(List.of(expectedGame));

    List<GameDto> actualGames = gameService.getGames();
    assertThat(actualGames).hasSize(1);
    isGameDtoEqualToGame(actualGames.get(0), expectedGame);

    final Player creator2 = new Player("2", "1");
    final Game expectedGame2 = new Game("id2", creator2);
    when((mockGameRepository.findAll())).thenReturn(List.of(expectedGame, expectedGame2));
    actualGames = gameService.getGames();
    assertThat(actualGames).hasSize(2);
    isGameDtoEqualToGame(actualGames.get(0), expectedGame);
    isGameDtoEqualToGame(actualGames.get(1), expectedGame2); // We know the order here
  }

  private static void isGameDtoEqualToGame(GameDto dto, Game game) {
    assertThat(dto).isNotNull();
    assertThat(game).isNotNull();
    assertThat(dto.id()).isEqualTo(game.getId());
    assertThat(dto.creator().name()).isEqualTo(game.getCreator().name());
    assertThat(dto.state()).isEqualTo(game.getState());
    final Map<String, PlayerDto> participants = game.getParticipants()
      .entrySet().stream()
      .collect(Collectors.toMap(Map.Entry::getKey, entry -> new PlayerDto(entry.getValue().id(), entry.getValue().id())));
    assertThat(dto.participants()).isEqualTo(participants);
    assertThat(dto.votes()).isEqualTo(game.getVotes());
  }
}
