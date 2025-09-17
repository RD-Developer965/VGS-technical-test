package com.vgs.web_service.presentation.dto;

import com.vgs.web_service.domain.model.Cell;
import com.vgs.web_service.domain.model.CellValue;
import com.vgs.web_service.domain.model.Game;
import com.vgs.web_service.domain.model.GameStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class GameResponse {
    private Long id;
    private LocalDateTime createdAt;
    private GameStatus status;
    private CellValue currentTurn;
    private List<CellDto> board;

    @Data
    @Builder
    public static class CellDto {
        private int row;
        private int column;
        private CellValue value;

        public static CellDto fromDomain(Cell cell) {
            return CellDto.builder()
                    .row(cell.getRow_number())
                    .column(cell.getColumn_number())
                    .value(cell.getValue())
                    .build();
        }
    }

    public static GameResponse fromDomain(Game game) {
        return GameResponse.builder()
                .id(game.getId())
                .createdAt(game.getCreatedAt())
                .status(game.getStatus())
                .currentTurn(game.getCurrentTurn())
                .board(game.getCells().stream()
                        .map(CellDto::fromDomain)
                        .collect(Collectors.toList()))
                .build();
    }
}