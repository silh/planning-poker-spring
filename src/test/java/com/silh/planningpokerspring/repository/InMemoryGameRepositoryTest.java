package com.silh.planningpokerspring.repository;

import com.silh.planningpokerspring.converter.PlayerConverter;
import com.silh.planningpokerspring.converter.RoundResultConverter;
import com.silh.planningpokerspring.domain.Game;
import com.silh.planningpokerspring.domain.Player;
import com.silh.planningpokerspring.service.StringIdGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class InMemoryGameRepositoryTest {

  private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
  private final PlayerConverter playerConverter = new PlayerConverter();
  private final RoundResultConverter roundResultConverter = new RoundResultConverter(playerConverter);
  private final GameRepository gameRepository = new HashMapGameRepository(new StringIdGenerator(), eventPublisher, playerConverter, roundResultConverter);

  @Test
  void canCreateNewGame() {
    final Player player = getGenericPlayer();
    final Game game = gameRepository.create("name", player);
    assertThat(game).isNotNull();
    assertThat(game.getCreator()).isEqualTo(player);
    assertThat(game.getId()).isNotNull();
  }

  @Test
  void idsShouldBeUnique() {
    final Game game1 = gameRepository.create("name", getGenericPlayer());
    final Game game2 = gameRepository.create("name2", getGenericPlayer());
    assertThat(game1.getId()).isNotEqualTo(game2.getId());
  }

  @Test
  void canGetCreatedGame() {
    final Game game = gameRepository.create("name", getGenericPlayer());

    final Optional<Game> getGameResult = gameRepository.find(game.getId());
    assertThat(getGameResult)
      .isPresent()
      .hasValue(game);
  }

  @Test
  void updatingNonexistentGameReturnEmpty() {
    final Optional<Game> updatedGame = gameRepository.update(new Game("someId", "name", getGenericPlayer(), eventPublisher, playerConverter, roundResultConverter));
    assertThat(updatedGame)
      .isNotPresent();
  }

  @Test
  void canDeleteExistingGame() {
    final Game game = gameRepository.create("name", getGenericPlayer());
    gameRepository.delete(game.getId());

    final Optional<Game> getGameResult = gameRepository.find(game.getId());
    assertThat(getGameResult)
      .isNotPresent();
  }

  private Player getGenericPlayer() {
    return new Player("someId", "someName");
  }
}
