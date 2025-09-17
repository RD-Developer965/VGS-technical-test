package com.vgs.web_service.presentation.controller;

import com.vgs.web_service.application.service.GameService;
import com.vgs.web_service.domain.exception.GameNotFoundException;
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
}
