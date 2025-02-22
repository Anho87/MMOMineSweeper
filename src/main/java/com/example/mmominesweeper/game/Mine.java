package com.example.mmominesweeper.game;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Mine {
    private final int x; 
    private final int y; 
    
    public Mine(int x, int y) {
        this.x = x;
        this.y = y;
    }

}
