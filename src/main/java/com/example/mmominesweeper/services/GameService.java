package com.example.mmominesweeper.services;

import com.example.mmominesweeper.game.GameUpdate;
import com.example.mmominesweeper.game.Mine;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Getter
@Setter
public class GameService {
    private final int boardSize = 50;
    private boolean[][] mines; 
    private boolean[][] revealed; 
    private List<Mine> mineList;
    private final int numberOfMines = (int) (boardSize * Math.sqrt(boardSize) * 0.70);
    private final int startingLife = 10;
    private int life = 0;
//    private final int numberOfMines = 1;

    public void initializeGame() {
        System.out.println("Number of mines " + numberOfMines);
        mines = new boolean[boardSize][boardSize];
        revealed = new boolean[boardSize][boardSize];
        mineList = new ArrayList<>();
        life = startingLife;
        
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
    public List<GameUpdate> getCurrentGameState() {
        List<GameUpdate> currentGameState = new ArrayList<>();
        for (int x = 0; x < boardSize; x++) {
            for (int y = 0; y < boardSize; y++) {
                if (revealed[x][y]) {
                    int adjacentMines = countAdjacentMines(x, y);
                    int cellIndex = x + (y * boardSize);
                    currentGameState.add(new GameUpdate(cellIndex, mines[x][y], adjacentMines, false, true));
                }
            }
        }
        return currentGameState;
    }
    public void resetGame() {
        mines = new boolean[boardSize][boardSize];
        revealed = new boolean[boardSize][boardSize];
        life = startingLife;
        mineList.clear(); 
        initializeGame(); 
    }

    public boolean isMineAt(int x, int y) {
        return mines[x][y];
    }
    
    public boolean isLifeZero(){
        System.out.println(life);
        return life == 0;
    }
    
    public void adjustLife(String operation){
        operation = operation.trim();
        if (operation.equals("+")) {
            life++;
        } else if (operation.equals("-")) {
            life--;
        }
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
        final int MAX_REVEAL_LIMIT = boardSize * boardSize;
        int revealCount = 0;

        queue.add(new int[]{startX, startY});

        while (!queue.isEmpty()) {
            if (revealCount >= MAX_REVEAL_LIMIT) {
                break;
            }

            int[] current = queue.poll();
            int x = current[0];
            int y = current[1];

            if (revealed[x][y]) {
                continue;  
            }

            revealCell(x, y);
            revealCount++;  
            int cellIndex = x + (y * boardSize);
            int adjacentMines = countAdjacentMines(x, y);
            revealedCellsUpdates.add(new GameUpdate(cellIndex, false, adjacentMines, false, false));

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
