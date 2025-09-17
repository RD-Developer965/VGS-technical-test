package com.vgs.web_service.infrastructure.persistence;

import com.vgs.web_service.domain.model.Game;
import com.vgs.web_service.domain.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GameRepositoryImpl implements GameRepository {
    private final JpaGameRepository jpaGameRepository;

    @Override
    public Game save(Game game) {
        return jpaGameRepository.save(game);
    }

    @Override
    public Game findById(Long id) {
        return jpaGameRepository.findById(id).orElse(null);
    }
}