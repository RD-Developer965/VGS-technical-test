package com.vgs.web_service.presentation.controller;

import com.vgs.web_service.application.service.GameService;
import com.vgs.web_service.domain.exception.GameNotFoundException;
import com.vgs.web_service.domain.exception.InvalidMoveException;
import com.vgs.web_service.domain.model.CellValue;
import com.vgs.web_service.domain.model.Game;
import com.vgs.web_service.domain.model.GameStatus;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class GameControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GameService gameService;

    @InjectMocks
    private GameController gameController;

    @Test
    void createGame_ShouldReturnNewGame() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(gameController).build();

        Game mockGame = Game.builder()
                .id(1L)
                .build();
        mockGame.initializeBoard();

        when(gameService.createGame()).thenReturn(mockGame);

        mockMvc.perform(post("/api/games/create"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value(GameStatus.IN_PROGRESS.toString()))
                .andExpect(jsonPath("$.currentTurn").value(CellValue.X.toString()))
                .andExpect(jsonPath("$.board").isArray())
                .andExpect(jsonPath("$.board.length()").value(9));
    }    

    @Test
    void getGameStatus_ShouldReturnGameStatusForValidId() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(gameController).build();

        Long validGameId = 1L;
        Game mockGame = Game.builder()
                .id(validGameId)
                .build();
        mockGame.initializeBoard();

        when(gameService.getGame(validGameId)).thenReturn(mockGame);

        mockMvc.perform(get("/api/games/status").param("matchId", validGameId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(validGameId))
                .andExpect(jsonPath("$.status").value(GameStatus.IN_PROGRESS.toString()))
                .andExpect(jsonPath("$.currentTurn").value(CellValue.X.toString()))
                .andExpect(jsonPath("$.board").isArray())
                .andExpect(jsonPath("$.board.length()").value(9));
    }

    @Test
    void getGameStatus_ShouldReturnNotFoundForInvalidId() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(gameController).build();

        Long invalidGameId = 123L;

        when(gameService.getGame(invalidGameId)).thenThrow(new GameNotFoundException(invalidGameId));

        mockMvc.perform(get("/api/games/status").param("matchId", invalidGameId.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Game with id " + invalidGameId + " not found"))
                .andExpect(jsonPath("$.path").value("/api/games/status"));
    }

    @Test
    void makeMove_ShouldUpdateGameStateWhenMoveIsValid() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(gameController).build();

        Long gameId = 1L;
        Game updatedGame = Game.builder()
                .id(gameId)
                .build();
        updatedGame.initializeBoard();
        updatedGame.makeMove(CellValue.X, 1, 1);

        when(gameService.makeMove(gameId, CellValue.X, 1, 1)).thenReturn(updatedGame);

        mockMvc.perform(post("/api/games/move")
                .contentType("application/json")
                .content("{\"matchId\":1,\"playerId\":\"X\",\"square\":{\"x\":1,\"y\":1}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(gameId))
                .andExpect(jsonPath("$.currentTurn").value(CellValue.O.toString()))
                .andExpect(jsonPath("$.status").value(GameStatus.IN_PROGRESS.toString()));
    }

    @Test
    void makeMove_ShouldReturnNotFoundWhenGameDoesNotExist() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(gameController).build();

        Long gameId = 123L;
        when(gameService.makeMove(gameId, CellValue.X, 1, 1))
                .thenThrow(new GameNotFoundException(gameId));

        mockMvc.perform(post("/api/games/move")
                .contentType("application/json")
                .content("{\"matchId\":123,\"playerId\":\"X\",\"square\":{\"x\":1,\"y\":1}}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Game with id " + gameId + " not found"));
    }

    @Test
    void makeMove_ShouldReturnBadRequestWhenMoveIsInvalid() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(gameController).build();

        Long gameId = 1L;
        when(gameService.makeMove(gameId, CellValue.X, 1, 1))
                .thenThrow(new InvalidMoveException("Cell at position (1,1) is already occupied"));

        mockMvc.perform(post("/api/games/move")
                .contentType("application/json")
                .content("{\"matchId\":1,\"playerId\":\"X\",\"square\":{\"x\":1,\"y\":1}}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Cell at position (1,1) is already occupied"));
    }

    @Test
    void makeMove_ShouldReturnGameStatusWhenPlayerWins() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(gameController).build();

        Long gameId = 1L;
        Game winningGame = Game.builder()
                .id(gameId)
                .build();
        winningGame.initializeBoard();
        // Simular una victoria
        winningGame.makeMove(CellValue.X, 1, 1);
        winningGame.makeMove(CellValue.O, 2, 1);
        winningGame.makeMove(CellValue.X, 1, 2);
        winningGame.makeMove(CellValue.O, 2, 2);
        winningGame.makeMove(CellValue.X, 1, 3); // Movimiento ganador

        when(gameService.makeMove(gameId, CellValue.X, 1, 3)).thenReturn(winningGame);

        mockMvc.perform(post("/api/games/move")
                .contentType("application/json")
                .content("{\"matchId\":1,\"playerId\":\"X\",\"square\":{\"x\":1,\"y\":3}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(GameStatus.X_WON.toString()));
    }

    @Test
    void makeMove_ShouldReturnGameStatusWhenDraw() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(gameController).build();

        Long gameId = 1L;
        Game drawGame = Game.builder()
                .id(gameId)
                .build();
        drawGame.initializeBoard();
        // Simular un empate
        drawGame.makeMove(CellValue.X, 1, 1);
        drawGame.makeMove(CellValue.O, 1, 2);
        drawGame.makeMove(CellValue.X, 1, 3);
        drawGame.makeMove(CellValue.O, 2, 2);
        drawGame.makeMove(CellValue.X, 2, 1);
        drawGame.makeMove(CellValue.O, 2, 3);
        drawGame.makeMove(CellValue.X, 3, 2);
        drawGame.makeMove(CellValue.O, 3, 1);
        drawGame.makeMove(CellValue.X, 3, 3); // Último movimiento que lleva al empate

        when(gameService.makeMove(gameId, CellValue.X, 3, 3)).thenReturn(drawGame);

        mockMvc.perform(post("/api/games/move")
                .contentType("application/json")
                .content("{\"matchId\":1,\"playerId\":\"X\",\"square\":{\"x\":3,\"y\":3}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(GameStatus.DRAW.toString()));
    }
}
