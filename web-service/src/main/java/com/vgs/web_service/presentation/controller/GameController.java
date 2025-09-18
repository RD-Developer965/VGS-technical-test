package com.vgs.web_service.presentation.controller;

import com.vgs.web_service.application.service.GameService;
import com.vgs.web_service.domain.exception.GameNotFoundException;
import com.vgs.web_service.domain.exception.InvalidMoveException;
import com.vgs.web_service.domain.model.Game;
import com.vgs.web_service.presentation.dto.ErrorResponse;
import com.vgs.web_service.presentation.dto.GameResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.validation.Valid;
import com.vgs.web_service.presentation.dto.MoveRequest;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;
    private static final Logger log = LoggerFactory.getLogger(GameController.class);

    @PostMapping("/create")
    public ResponseEntity<GameResponse> createGame() {
        return ResponseEntity.ok(GameResponse.fromDomain(gameService.createGame()));
    }
    
    @GetMapping("/status")
    public ResponseEntity<?> getGameStatus(@RequestParam Long matchId, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(GameResponse.fromDomain(gameService.getGame(matchId)));
        } catch (GameNotFoundException ex) {
            log.warn("Game not found: gameId={}", matchId);
            ErrorResponse error = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.NOT_FOUND.value())
                    .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                    .message(ex.getMessage())
                    .path(request.getRequestURI())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @PostMapping("/move")
    public ResponseEntity<?> makeMove(@Valid @RequestBody MoveRequest moveRequest, HttpServletRequest request) {
        try {
            Game game = gameService.makeMove(moveRequest.getMatchId(), moveRequest.getPlayerId(), 
                    moveRequest.getSquare().getX(), moveRequest.getSquare().getY());
            return ResponseEntity.ok(GameResponse.fromDomain(game));
        } catch (GameNotFoundException ex) {
            ErrorResponse error = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.NOT_FOUND.value())
                    .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                    .message(ex.getMessage())
                    .path(request.getRequestURI())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (InvalidMoveException ex) {
            log.error("Invalid move", ex);
            ErrorResponse error = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.BAD_REQUEST.value())
                    .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                    .message(ex.getMessage())
                    .path(request.getRequestURI())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}