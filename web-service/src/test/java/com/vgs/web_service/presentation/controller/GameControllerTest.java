package com.vgs.web_service.presentation.controller;

import com.vgs.web_service.application.service.GameService;
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
}
