package com.example.mmominesweeper.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameUpdate {
    
    private int cellIndex; 
    private final boolean mine; 
    private final int adjacentMines; 
    private final boolean win; 
    private final boolean resetGame;

    public GameUpdate(int cellIndex, boolean mine,int adjacentMines, boolean win, boolean resetGame) {
        this.cellIndex = cellIndex;
        this.mine = mine;
        this.adjacentMines = adjacentMines;
        this.win = win;
        this.resetGame = resetGame;
    }
}

