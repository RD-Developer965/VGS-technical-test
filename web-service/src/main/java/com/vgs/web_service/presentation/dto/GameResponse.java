package com.vgs.web_service.presentation.dto;

import com.vgs.web_service.domain.model.GameStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GameResponse {
    private Long id;
    private LocalDateTime createdAt;
    private GameStatus status;

    public static GameResponse fromDomain(com.vgs.web_service.domain.model.Game game) {
        return GameResponse.builder()
                .id(game.getId())
                .createdAt(game.getCreatedAt())
                .status(game.getStatus())
                .build();
    }
}