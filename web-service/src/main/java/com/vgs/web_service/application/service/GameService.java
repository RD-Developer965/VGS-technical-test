package com.vgs.web_service.application.service;

import com.vgs.web_service.domain.exception.GameNotFoundException;
import com.vgs.web_service.domain.model.CellValue;
import com.vgs.web_service.domain.model.Game;
import com.vgs.web_service.domain.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private static final Logger log = LoggerFactory.getLogger(GameService.class);

    @Transactional
    public Game createGame() {

        Game game = Game.builder().build();

        log.info("Creating new game...");

        return gameRepository.save(game);
    }

    public Game getGame(Long id) {

        Game game = gameRepository.findById(id);

        if (game == null) {
            throw new GameNotFoundException(id);
        }

        return game;
    }

    @Transactional
    public Game makeMove(Long gameId, CellValue playerId, Integer x, Integer y) {

        Game game = getGame(gameId);
        
        game.makeMove(playerId, x, y);

        log.info("Move successful: gameId={}, player={}, position=({},{})", gameId, playerId, x, y);

        return gameRepository.save(game);
                        
    }
}