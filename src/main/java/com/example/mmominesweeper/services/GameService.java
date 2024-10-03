package com.example.mmominesweeper.services;

import com.example.mmominesweeper.models.GameUpdate;
import com.example.mmominesweeper.models.Mine;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Getter
@Setter
public class GameService {
    private final int boardSize = 10;
    private boolean[][] mines; 
    private boolean[][] revealed; 
    private List<Mine> mineList; 
    private final int numberOfMines = 10;

    public void initializeGame() {
        mines = new boolean[boardSize][boardSize];
        revealed = new boolean[boardSize][boardSize];
        mineList = new ArrayList<>();
        
        Random rand = new Random();

        while (mineList.size() < numberOfMines) {
            int x = rand.nextInt(boardSize);
            int y = rand.nextInt(boardSize);
            if (!mines[x][y]) {
                mines[x][y] = true; 
                mineList.add(new Mine(x, y)); 
            }
        }
    }

    public void resetGame() {
        mines = new boolean[boardSize][boardSize];
        revealed = new boolean[boardSize][boardSize];
        mineList.clear(); 
        initializeGame(); 
    }

    public boolean isMineAt(int x, int y) {
        return mines[x][y];
    }

    public void revealCell(int x, int y) {
        revealed[x][y] = true;
    }

    public int countAdjacentMines(int x, int y) {
        int count = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;

                int newX = x + dx;
                int newY = y + dy;
                if (newX >= 0 && newX < boardSize && newY >= 0 && newY < boardSize) {
                    if (mines[newX][newY]) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public List<GameUpdate> revealAdjacentCells(int startX, int startY) {
        List<GameUpdate> revealedCellsUpdates = new ArrayList<>();
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{startX, startY});

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0];
            int y = current[1];
            revealCell(x, y);
            int cellIndex = x + (y * boardSize);
            int adjacentMines = countAdjacentMines(x, y);
            revealedCellsUpdates.add(new GameUpdate(cellIndex, false, adjacentMines, false,false));
            
            if (adjacentMines == 0) {
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if (dx == 0 && dy == 0) continue; 
                        int newX = x + dx;
                        int newY = y + dy;
                        if (newX >= 0 && newX < boardSize && newY >= 0 && newY < boardSize && !revealed[newX][newY]) {
                            queue.add(new int[]{newX, newY}); 
                        }
                    }
                }
            }
        }
        return revealedCellsUpdates; 
    }

    public boolean checkWinCondition() {
        for (int x = 0; x < boardSize; x++) {
            for (int y = 0; y < boardSize; y++) {
                if (!mines[x][y] && !revealed[x][y]) {
                    return false;
                }
            }
        }
        return true; 
    }
}
