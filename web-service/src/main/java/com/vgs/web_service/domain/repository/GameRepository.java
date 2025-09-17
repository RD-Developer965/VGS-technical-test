package com.vgs.web_service.domain.repository;

import com.vgs.web_service.domain.model.Game;

public interface GameRepository {
    Game save(Game game);
}