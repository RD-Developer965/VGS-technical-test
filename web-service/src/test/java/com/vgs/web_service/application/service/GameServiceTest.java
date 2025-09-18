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

    @Test
    void makeMove_ShouldUpdateGameStateWhenMoveIsValid() {
        // Given
        Long gameId = 1L;
        Game game = Game.builder()
                .id(gameId)
                .build();
        game.initializeBoard();

        Game updatedGame = Game.builder()
                .id(gameId)
                .build();
        updatedGame.initializeBoard();
        updatedGame.makeMove(CellValue.X, 1, 1);

        when(gameRepository.findById(gameId)).thenReturn(game);
        when(gameRepository.save(any(Game.class))).thenReturn(updatedGame);

        // When
        Game result = gameService.makeMove(gameId, CellValue.X, 1, 1);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(gameId);
        assertThat(result.getCurrentTurn()).isEqualTo(CellValue.O);
        assertThat(result.getCells().get(0).getValue()).isEqualTo(CellValue.X);
    }

    @Test
    void makeMove_ShouldThrowExceptionWhenGameNotFound() {
        // Given
        Long gameId = 1L;
        when(gameRepository.findById(gameId)).thenReturn(null);

        // When / Then
        try {
            gameService.makeMove(gameId, CellValue.X, 1, 1);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(com.vgs.web_service.domain.exception.GameNotFoundException.class);
            assertThat(e.getMessage()).isEqualTo("Game with id " + gameId + " not found");
        }
    }

    @Test
    void makeMove_ShouldThrowExceptionWhenMoveIsInvalid() {
        // Given
        Long gameId = 1L;
        Game game = Game.builder()
                .id(gameId)
                .build();
        game.initializeBoard();
        game.makeMove(CellValue.X, 1, 1);

        when(gameRepository.findById(gameId)).thenReturn(game);

        // When / Then
        try {
            gameService.makeMove(gameId, CellValue.O, 1, 1);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(com.vgs.web_service.domain.exception.InvalidMoveException.class);
            assertThat(e.getMessage()).contains("Cell at position (1,1) is already occupied");
        }
    }

    @Test
    void makeMove_ShouldThrowExceptionWhenNotPlayersTurn() {
        // Given
        Long gameId = 1L;
        Game game = Game.builder()
                .id(gameId)
                .build();
        game.initializeBoard();

        when(gameRepository.findById(gameId)).thenReturn(game);

        // When / Then
        try {
            gameService.makeMove(gameId, CellValue.O, 1, 1);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(com.vgs.web_service.domain.exception.InvalidMoveException.class);
            assertThat(e.getMessage()).contains("It's not player O's turn. Current turn: X");
        }
    }

    @Test
    void makeMove_ShouldEndGameWhenPlayerWins() {
        // Given
        Long gameId = 1L;
        Game game = Game.builder()
                .id(gameId)
                .build();
        game.initializeBoard();


        game.makeMove(CellValue.X, 1, 1); // X (1,1)
        game.makeMove(CellValue.O, 2, 1); // O (2,1)
        game.makeMove(CellValue.X, 1, 2); // X (1,2)
        game.makeMove(CellValue.O, 2, 2); // O (2,2)

        when(gameRepository.findById(gameId)).thenReturn(game);
        when(gameRepository.save(any(Game.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Game result = gameService.makeMove(gameId, CellValue.X, 1, 3);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(GameStatus.X_WON);
    }

    @Test
    void makeMove_ShouldEndGameWhenDraw() {
        // Given
        Long gameId = 1L;
        Game game = Game.builder()
                .id(gameId)
                .build();
        game.initializeBoard();

        game.makeMove(CellValue.X, 1, 1); // X (1,1)
        game.makeMove(CellValue.O, 1, 2); // O (1,2)
        game.makeMove(CellValue.X, 1, 3); // X (1,3)
        game.makeMove(CellValue.O, 2, 2); // O (2,2)
        game.makeMove(CellValue.X, 2, 1); // X (2,1)
        game.makeMove(CellValue.O, 2, 3); // O (2,3)
        game.makeMove(CellValue.X, 3, 2); // X (3,2)
        game.makeMove(CellValue.O, 3, 1); // O (3,1)

        when(gameRepository.findById(gameId)).thenReturn(game);
        when(gameRepository.save(any(Game.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Game result = gameService.makeMove(gameId, CellValue.X, 3, 3);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(GameStatus.DRAW);
        assertThat(result.getCells()).allMatch(cell -> cell.getValue() != CellValue.EMPTY);
    }
}