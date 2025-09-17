package com.vgs.web_service.application.service;

import com.vgs.web_service.domain.model.Game;
import com.vgs.web_service.domain.model.GameStatus;
import com.vgs.web_service.domain.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;

    @Transactional
    public Game createGame() {
        Game game = Game.builder()
                .status(GameStatus.IN_PROGRESS)
                .build();
        return gameRepository.save(game);
    }
}