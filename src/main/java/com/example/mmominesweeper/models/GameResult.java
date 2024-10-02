package com.example.mmominesweeper.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;


import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class GameResult {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private boolean isWin;
    private LocalDateTime timestamp;

    public GameResult() {}

    public GameResult(boolean isWin) {
        this.isWin = isWin;
        this.timestamp = LocalDateTime.now();
    }
}
