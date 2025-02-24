package com.example.mmominesweeper.game;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameUpdate {
    
    private int cellIndex; 
    private final boolean mine; 
    private final int adjacentMines; 
    private final boolean win;
    private final boolean gameState;
    private final int life;

    public GameUpdate(int cellIndex, boolean mine, int adjacentMines, boolean win, boolean gameState, int life) {
        this.cellIndex = cellIndex;
        this.mine = mine;
        this.adjacentMines = adjacentMines;
        this.win = win;
        this.gameState = gameState;
        this.life = life;
    }
}

