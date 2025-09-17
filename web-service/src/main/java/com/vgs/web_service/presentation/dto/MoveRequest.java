package com.vgs.web_service.presentation.dto;

import com.vgs.web_service.domain.model.CellValue;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MoveRequest {
    @NotNull(message = "matchId is required")
    private Long matchId;

    @NotNull(message = "playerId is required")
    private CellValue playerId;

    @NotNull(message = "square is required")
    @Valid
    private Square square;

    @Data
    public static class Square {
        @NotNull(message = "x coordinate is required")
        @Min(value = 1, message = "x coordinate must be between 1 and 3")
        @Max(value = 3, message = "x coordinate must be between 1 and 3")
        private Integer x;

        @NotNull(message = "y coordinate is required")
        @Min(value = 1, message = "y coordinate must be between 1 and 3")
        @Max(value = 3, message = "y coordinate must be between 1 and 3")
        private Integer y;
    }
}