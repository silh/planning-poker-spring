package com.silh.planningpokerspring.repository;

import com.silh.planningpokerspring.Game;
import com.silh.planningpokerspring.Player;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryGameRepositoryTest {

  private final GameRepository gameRepository = new GenericGameRepository();

  @Test
  void canCreateNewGame() {
    final Player player = getGenericPlayer();
    final Game game = gameRepository.create(player);
    assertThat(game).isNotNull();
    assertThat(game.getCreator()).isEqualTo(player);
    assertThat(game.getId()).isNotNull();
  }

  @Test
  void idsShouldBeUnique() {
    final Game game1 = gameRepository.create(getGenericPlayer());
    final Game game2 = gameRepository.create(getGenericPlayer());
    assertThat(game1.getId()).isNotEqualTo(game2.getId());
  }

  @Test
  void canGetCreatedGame() {
    final Game game = gameRepository.create(getGenericPlayer());

    final Optional<Game> getGameResult = gameRepository.find(game.getId());
    assertThat(getGameResult)
      .isPresent()
      .hasValue(game);
  }

  @Test
  void updatingNonexistentGameReturnEmpty() {
    final Optional<Game> updatedGame = gameRepository.update(new Game("someId", getGenericPlayer()));
    assertThat(updatedGame)
      .isNotPresent();
  }

  @Test
  void canDeleteExistingGame() {
    final Game game = gameRepository.create(getGenericPlayer());
    gameRepository.delete(game.getId());

    final Optional<Game> getGameResult = gameRepository.find(game.getId());
    assertThat(getGameResult)
      .isNotPresent();
  }

  private Player getGenericPlayer() {
    return new Player("someId", "someName");
  }
}
