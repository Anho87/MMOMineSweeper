package com.example.mmominesweeper.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameUpdate {
    // Getters och Setters
    
    private int cellIndex; // Cellens index
    private final boolean mine; // Anger om cellen innehåller en mina
    private final int adjacentMines; // Antal angränsande miner
    private final boolean win; // Anger om spelaren har vunnit

    public GameUpdate(int cellIndex, boolean mine,int adjacentMines, boolean win) {
        this.cellIndex = cellIndex;
        this.mine = mine;
        this.adjacentMines = adjacentMines;
        this.win = win;
    }
}

