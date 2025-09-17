package com.vgs.web_service.application.service;

import com.vgs.web_service.domain.model.CellValue;
import com.vgs.web_service.domain.model.Game;
import com.vgs.web_service.domain.model.GameStatus;
import com.vgs.web_service.domain.repository.GameRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameService gameService;

    @Test
    void createGame_ShouldCreateNewGameWithInProgressStatus() {
        // Given
        Game expectedGame = Game.builder()
                .id(1L)
                .build();
        expectedGame.initializeBoard();

        when(gameRepository.save(any(Game.class))).thenReturn(expectedGame);

        // When
        Game result = gameService.createGame();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(GameStatus.IN_PROGRESS);
        assertThat(result.getCurrentTurn()).isEqualTo(CellValue.X);
        assertThat(result.getCells()).isNotNull();
        assertThat(result.getCells().size()).isEqualTo(9);
        
    }

    @Test
    void getGame_ShouldReturnGameWhenFound() {
        // Given
        Long gameId = 1L;
        Game expectedGame = Game.builder()
                .id(gameId)
                .build();
        expectedGame.initializeBoard();

        when(gameRepository.findById(gameId)).thenReturn(expectedGame);

        // When
        Game result = gameService.getGame(gameId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(gameId);
    }

    @Test
    void getGame_ShouldThrowExceptionWhenNotFound() {
        // Given
        Long gameId = 1L;

        when(gameRepository.findById(gameId)).thenReturn(null);

        // When / Then
        try {
            gameService.getGame(gameId);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(com.vgs.web_service.domain.exception.GameNotFoundException.class);
            assertThat(e.getMessage()).isEqualTo("Game with id " + gameId + " not found");
        }
    }
}