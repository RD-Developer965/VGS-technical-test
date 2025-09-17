package com.vgs.web_service.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vgs.web_service.domain.model.Game;

interface JpaGameRepository extends JpaRepository<Game, Long> {
}