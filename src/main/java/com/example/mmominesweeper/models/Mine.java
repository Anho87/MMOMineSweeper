package com.example.mmominesweeper.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Mine {
    // Getters
    private final int x; // X-koordinat för minan
    private final int y; // Y-koordinat för minan

    // Konstruktor
    public Mine(int x, int y) {
        this.x = x;
        this.y = y;
    }

}
