package com.vgs.web_service.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cells")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cell {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(nullable = false)
    private Integer row_number;

    @Column(nullable = false)
    private Integer column_number;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CellValue value = CellValue.EMPTY;
}