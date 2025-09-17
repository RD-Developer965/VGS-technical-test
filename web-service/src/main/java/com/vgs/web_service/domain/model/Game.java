package com.vgs.web_service.domain.model;

import com.vgs.web_service.domain.exception.InvalidMoveException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "games")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private GameStatus status = GameStatus.IN_PROGRESS;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CellValue currentTurn = CellValue.X;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Cell> cells = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        initializeBoard();
    }

    public void initializeBoard() {
        if (cells.isEmpty()) {
            for (int row = 1; row <= 3; row++) {
                for (int col = 1; col <= 3; col++) {
                    cells.add(Cell.builder()
                            .game(this)
                            .row_number(row)
                            .column_number(col)
                            .value(CellValue.EMPTY)
                            .build());
                }
            }
        }
    }

    public void makeMove(CellValue playerId, Integer x, Integer y) {
        validateMove(playerId, x, y);
        cells.stream()
                .filter(cell -> cell.getRow_number().equals(x) && cell.getColumn_number().equals(y))
                .findFirst()
                .ifPresent(cell -> cell.setValue(playerId));
        currentTurn = (currentTurn == CellValue.X) ? CellValue.O : CellValue.X;
    }

    private void validateMove(CellValue playerId, Integer x, Integer y) {
        if (status != GameStatus.IN_PROGRESS) {
            throw new InvalidMoveException("Game is already finished");
        }
        if (!playerId.equals(currentTurn)) {
            throw new InvalidMoveException("It's not player " + playerId + "'s turn. Current turn: " + currentTurn);
        }
        boolean cellOccupied = cells.stream()
                .anyMatch(cell -> cell.getRow_number().equals(x) 
                        && cell.getColumn_number().equals(y) 
                        && cell.getValue() != CellValue.EMPTY);
        if (cellOccupied) {
            throw new InvalidMoveException("Cell at position (" + x + "," + y + ") is already occupied");
        }
    }
}