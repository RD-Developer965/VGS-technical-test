package com.vgs.web_service.domain.model;

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
}