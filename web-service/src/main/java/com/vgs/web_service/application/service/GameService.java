package com.vgs.web_service.application.service;

import com.vgs.web_service.domain.exception.GameNotFoundException;
import com.vgs.web_service.domain.model.Game;
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
        Game game = Game.builder().build();
        return gameRepository.save(game);
    }

    public Game getGame(Long id) {
        Game game = gameRepository.findById(id);
        if (game == null) {
            throw new GameNotFoundException(id);
        }
        return game;
    }
}