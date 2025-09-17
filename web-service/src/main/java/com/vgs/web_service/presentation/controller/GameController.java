package com.vgs.web_service.presentation.controller;

import com.vgs.web_service.application.service.GameService;
import com.vgs.web_service.domain.exception.GameNotFoundException;
import com.vgs.web_service.presentation.dto.ErrorResponse;
import com.vgs.web_service.presentation.dto.GameResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;

    @PostMapping("/create")
    public ResponseEntity<GameResponse> createGame() {
        return ResponseEntity.ok(GameResponse.fromDomain(gameService.createGame()));
    }
    
    @GetMapping("/status")
    public ResponseEntity<?> getGameStatus(@RequestParam Long matchId, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(GameResponse.fromDomain(gameService.getGame(matchId)));
        } catch (GameNotFoundException ex) {
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
}