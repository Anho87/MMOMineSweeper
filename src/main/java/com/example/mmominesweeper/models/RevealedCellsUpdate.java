package com.example.mmominesweeper.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RevealedCellsUpdate {
    
    private final List<GameUpdate> revealedCells;
    private boolean isWin;

    public RevealedCellsUpdate(List<GameUpdate> revealedCells, boolean isWin) {
        this.revealedCells = revealedCells;
        this.isWin = isWin;
    }

    public boolean isWin() {
        return isWin;
    }
}

