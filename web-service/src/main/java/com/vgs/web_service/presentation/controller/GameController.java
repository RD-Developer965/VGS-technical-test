package com.vgs.web_service.presentation.controller;

import com.vgs.web_service.application.service.GameService;
import com.vgs.web_service.presentation.dto.GameResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;

    @PostMapping("/create")
    public ResponseEntity<GameResponse> createGame() {
        return ResponseEntity.ok(GameResponse.fromDomain(gameService.createGame()));
    }
}