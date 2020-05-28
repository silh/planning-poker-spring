package com.silh.planningpokerspring.service;

import com.silh.planningpokerspring.Game;
import com.silh.planningpokerspring.Player;
import com.silh.planningpokerspring.RoundState;
import com.silh.planningpokerspring.converter.GameConverterImpl;
import com.silh.planningpokerspring.dto.GameDto;
import com.silh.planningpokerspring.repository.GameRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GenericGameServiceTest {
  private final GameRepository mockRepository = mock(GameRepository.class);
  private final List<GameEventSubscriber> subscribers = new ArrayList<>();
  private final GenericGameService genericGameService =
    new GenericGameService(mockRepository, new GameConverterImpl(), subscribers);

  @Test
  void canCreateGame() {
    final Player creator = new Player("1", "2");
    final Game createdGame = new Game("id", creator);
    when(mockRepository.create(creator)).thenReturn(createdGame);

    final GameDto result = genericGameService.createGame(creator);
    isGameDtoEqualToGame(result, createdGame);
  }

  @Test
  void canGetAGame() {
    final Player creator = new Player("1", "2");
    final Game expectedGame = new Game("id", creator);
    when(mockRepository.find(expectedGame.getId())).thenReturn(Optional.of(expectedGame));

    final Optional<GameDto> result = genericGameService.getGame(expectedGame.getId());
    assertThat(result).isPresent()
      .hasValueSatisfying(dto -> isGameDtoEqualToGame(dto, expectedGame));
  }

  @Test
  void getGameReturnEmptyOptionIfGameDoesntExist() {
    final String gameId = "1";
    when(mockRepository.find(gameId)).thenReturn(Optional.empty());

    final Optional<GameDto> result = genericGameService.getGame(gameId);
    assertThat(result).isNotPresent();
  }

  @Test
  void canJoinGame() {
    final Player creator = new Player("1", "1");
    final Game expectedGame = new Game("id", creator);
    final Player player = new Player("2", "2");
    when(mockRepository.find(expectedGame.getId())).thenReturn(Optional.of(expectedGame));

    final boolean joined = genericGameService.joinGame(expectedGame.getId(), player);
    assertThat(joined).isTrue();
    assertThat(expectedGame.getParticipants()).containsEntry("2", player);
  }

  @Test
  void cantJoinGameIfItDoesntExist() {
    final String gameId = "id";
    final Player player = new Player("2", "2");
    when(mockRepository.find(gameId)).thenReturn(Optional.empty());

    final boolean joined = genericGameService.joinGame(gameId, player);
    assertThat(joined).isFalse();
  }

  @Test
  void creatorCanTransitionGameToAnotherStage() {
    final Player creator = new Player("1", "1");
    final Game expectedGame = new Game("id", creator);
    when(mockRepository.findByIdAndOwnerId(expectedGame.getId(), creator.getId()))
      .thenReturn(Optional.of(expectedGame));

    final RoundState nextState = RoundState.VOTING;
    final boolean transitioned =
      genericGameService.transitionTo(expectedGame.getId(), creator.getId(), nextState);
    assertThat(transitioned).isTrue();
    assertThat(expectedGame.getState()).isEqualTo(nextState);
  }

  @Test
  void canVote() {
    final Player creator = new Player("1", "1");
    final Game expectedGame = new Game("id", creator);
    final Player voter = new Player("2", "voter");
    expectedGame.addParticipant(voter);
    when(mockRepository.find(expectedGame.getId()))
      .thenReturn(Optional.of(expectedGame));

    final boolean updated = genericGameService.vote(expectedGame.getId(), voter.getId(), 1L);
    assertThat(updated).isTrue();
    assertThat(expectedGame.getVotes()).containsEntry(voter.getId(), 1L);
  }

  @Test
  void cantVoteIfNotParticipant() {
    final Player creator = new Player("1", "1");
    final Game expectedGame = new Game("id", creator);
    final Player voter = new Player("2", "voter");
    when(mockRepository.find(expectedGame.getId()))
      .thenReturn(Optional.of(expectedGame));

    final boolean updated = genericGameService.vote(expectedGame.getId(), voter.getId(), 1L);
    assertThat(updated).isFalse();
    assertThat(expectedGame.getVotes()).doesNotContainKey(voter.getId());
  }

  private static void isGameDtoEqualToGame(GameDto dto, Game game) {
    assertThat(dto).isNotNull();
    assertThat(game).isNotNull();
    assertThat(dto.getId()).isEqualTo(game.getId());
    assertThat(dto.getCreator()).isEqualTo(game.getCreator());
    assertThat(dto.getState()).isEqualTo(game.getState());
    assertThat(dto.getParticipants()).isEqualTo(game.getParticipants());
    assertThat(dto.getVotes()).isEqualTo(game.getVotes());
  }
}
