package com.vgs.web_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.jayway.jsonpath.JsonPath;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class GameIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void createGame_ShouldCreateAndReturnNewGame() throws Exception {
        mockMvc.perform(post("/api/games/create"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.currentTurn").value("X"))
                .andExpect(jsonPath("$.board").isArray())
                .andExpect(jsonPath("$.board.length()").value(9));
    }

    @Test
    void getGameStatus_ShouldReturnGameStatusForValidId() throws Exception {
        // First, create a new game to get a valid game ID
        String response = mockMvc.perform(post("/api/games/create"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Number gameId = JsonPath.read(response, "$.id");

        // Now, retrieve the game status using the valid game ID
        mockMvc.perform(get("/api/games/status").param("matchId", gameId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(gameId))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.currentTurn").value("X"))
                .andExpect(jsonPath("$.board").isArray())
                .andExpect(jsonPath("$.board.length()").value(9));
    }

    @Test
    void getGameStatus_ShouldReturnNotFoundForInvalidId() throws Exception {
        Long invalidGameId = 9999L;

        mockMvc.perform(get("/api/games/status").param("matchId", invalidGameId.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Game with id " + invalidGameId + " not found"))
                .andExpect(jsonPath("$.path").value("/api/games/status"));
    }

    @Test
    void makeMove_ShouldUpdateGameStateWhenMoveIsValid() throws Exception {
        // First create a game
        String createResponse = mockMvc.perform(post("/api/games/create"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Number gameId = JsonPath.read(createResponse, "$.id");

        // Make a valid move
        mockMvc.perform(post("/api/games/move")
                .contentType("application/json")
                .content("{\"matchId\":" + gameId + ",\"playerId\":\"X\",\"square\":{\"x\":1,\"y\":1}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(gameId))
                .andExpect(jsonPath("$.currentTurn").value("O"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.board[0].value").value("X")); // Verificar que la celda se marcó
    }

    @Test
    void makeMove_ShouldReturnNotFoundWhenGameDoesNotExist() throws Exception {
        Long nonExistentGameId = 9999L;

        mockMvc.perform(post("/api/games/move")
                .contentType("application/json")
                .content("{\"matchId\":" + nonExistentGameId + ",\"playerId\":\"X\",\"square\":{\"x\":1,\"y\":1}}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Game with id " + nonExistentGameId + " not found"));
    }

    @Test
    void makeMove_ShouldReturnBadRequestWhenMoveIsInvalid() throws Exception {
        // First create a game
        String createResponse = mockMvc.perform(post("/api/games/create"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Number gameId = JsonPath.read(createResponse, "$.id");

        // Make first move
        mockMvc.perform(post("/api/games/move")
                .contentType("application/json")
                .content("{\"matchId\":" + gameId + ",\"playerId\":\"X\",\"square\":{\"x\":1,\"y\":1}}"))
                .andExpect(status().isOk());

        // Try to make a move in the same position
        mockMvc.perform(post("/api/games/move")
                .contentType("application/json")
                .content("{\"matchId\":" + gameId + ",\"playerId\":\"O\",\"square\":{\"x\":1,\"y\":1}}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Cell at position (1,1) is already occupied"));
    }

    @Test
    void makeMove_ShouldCompleteGameWithWinner() throws Exception {
        // First create a game
        String createResponse = mockMvc.perform(post("/api/games/create"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Number gameId = JsonPath.read(createResponse, "$.id");

        // Make moves to create a winning scenario for X (horizontal first row)
        mockMvc.perform(post("/api/games/move")
                .contentType("application/json")
                .content("{\"matchId\":" + gameId + ",\"playerId\":\"X\",\"square\":{\"x\":1,\"y\":1}}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/games/move")
                .contentType("application/json")
                .content("{\"matchId\":" + gameId + ",\"playerId\":\"O\",\"square\":{\"x\":2,\"y\":1}}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/games/move")
                .contentType("application/json")
                .content("{\"matchId\":" + gameId + ",\"playerId\":\"X\",\"square\":{\"x\":1,\"y\":2}}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/games/move")
                .contentType("application/json")
                .content("{\"matchId\":" + gameId + ",\"playerId\":\"O\",\"square\":{\"x\":2,\"y\":2}}"))
                .andExpect(status().isOk());

        // Winning move for X
        mockMvc.perform(post("/api/games/move")
                .contentType("application/json")
                .content("{\"matchId\":" + gameId + ",\"playerId\":\"X\",\"square\":{\"x\":1,\"y\":3}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("X_WON"));
    }

    @Test
    void makeMove_ShouldCompleteGameWithDraw() throws Exception {
        // First create a game
        String createResponse = mockMvc.perform(post("/api/games/create"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Number gameId = JsonPath.read(createResponse, "$.id");

        // Sequence of moves leading to a draw
        String[][] moves = {
            {"X", "1", "1"}, {"O", "1", "2"}, {"X", "1", "3"},
            {"O", "2", "2"}, {"X", "2", "1"}, {"O", "2", "3"},
            {"X", "3", "2"}, {"O", "3", "1"}
        };

        // Execute all moves except the last one
        for (String[] move : moves) {
            mockMvc.perform(post("/api/games/move")
                    .contentType("application/json")
                    .content("{\"matchId\":" + gameId + ",\"playerId\":\"" + move[0] + 
                            "\",\"square\":{\"x\":" + move[1] + ",\"y\":" + move[2] + "}}"))
                    .andExpect(status().isOk());
        }

        // Final move leading to draw
        mockMvc.perform(post("/api/games/move")
                .contentType("application/json")
                .content("{\"matchId\":" + gameId + ",\"playerId\":\"X\",\"square\":{\"x\":3,\"y\":3}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DRAW"));
    }
}