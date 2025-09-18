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

        CellValue winner = checkWinner();
        if (winner != null) {
            if (winner == CellValue.X) {
                this.status = GameStatus.X_WON;
            } else {
                this.status = GameStatus.O_WON;
            }
            return;
        }

        // Check for draw
        if (isDraw()) {
            this.status = GameStatus.DRAW;
            return;
        }

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

    private CellValue checkWinner() {
        // Check rows
        for (int row = 1; row <= 3; row++) {
            if (checkLine(row, 1, row, 2, row, 3)) {
                return getCellValue(row, 1);
            }
        }

        // Check columns
        for (int col = 1; col <= 3; col++) {
            if (checkLine(1, col, 2, col, 3, col)) {
                return getCellValue(1, col);
            }
        }

        // Check diagonals
        if (checkLine(1, 1, 2, 2, 3, 3) || checkLine(1, 3, 2, 2, 3, 1)) {
            return getCellValue(2, 2);
        }

        return null;
    }

    private boolean checkLine(int row1, int col1, int row2, int col2, int row3, int col3) {
        CellValue first = getCellValue(row1, col1);
        if (first == CellValue.EMPTY) return false;
        
        return first == getCellValue(row2, col2) && first == getCellValue(row3, col3);
    }

    private CellValue getCellValue(int row, int col) {
        return cells.stream()
                .filter(cell -> cell.getRow_number() == row && cell.getColumn_number() == col)
                .findFirst()
                .map(Cell::getValue)
                .orElse(CellValue.EMPTY);
    }

    private boolean isDraw() {
        return cells.stream().noneMatch(cell -> cell.getValue() == CellValue.EMPTY);
    }
}