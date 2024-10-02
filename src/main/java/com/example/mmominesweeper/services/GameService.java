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
    private final int boardSize = 10; // Storleken på brädet
    private boolean[][] mines; // Matris för att representera miner
    private boolean[][] revealed; // Matris för att representera avslöjade celler
    private List<Mine> mineList; // Lista över miner
    private final int numberOfMines = 10;

    public void initializeGame() {
        mines = new boolean[boardSize][boardSize];
        revealed = new boolean[boardSize][boardSize];
        mineList = new ArrayList<>();

        // Placera miner på brädet
        Random rand = new Random();

        while (mineList.size() < numberOfMines) {
            int x = rand.nextInt(boardSize);
            int y = rand.nextInt(boardSize);
            if (!mines[x][y]) {
                mines[x][y] = true; // Sätt en mina på positionen
                mineList.add(new Mine(x, y)); // Lägg till minan i listan
            }
        }
    }

    public void resetGame() {
        mines = new boolean[boardSize][boardSize];
        revealed = new boolean[boardSize][boardSize];
        mineList.clear(); // Töm listan över miner
        initializeGame(); // Återställ spelet genom att initiera det igen
    }

    public boolean isMineAt(int x, int y) {
        return mines[x][y];
    }

    public void revealCell(int x, int y) {
        revealed[x][y] = true;
    }

    public int countAdjacentMines(int x, int y) {
        int count = 0;
        // Kontrollera angränsande rutor
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                // Undvik att kontrollera den aktuella cellen
                if (dx == 0 && dy == 0) continue;

                int newX = x + dx;
                int newY = y + dy;

                // Kontrollera om positionen är inom brädet
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
            // Avslöja cellen
            revealCell(x, y);
            int cellIndex = x + (y * boardSize);
            int adjacentMines = countAdjacentMines(x, y);
            revealedCellsUpdates.add(new GameUpdate(cellIndex, false, adjacentMines, false)); // Skapa GameUpdate

            // Om det inte finns några angränsande miner, lägg till angränsande celler till kön
            if (adjacentMines == 0) {
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if (dx == 0 && dy == 0) continue; // Hoppa över den aktuella cellen
                        int newX = x + dx;
                        int newY = y + dy;

                        // Kontrollera att den nya cellen är inom gränserna och inte redan avslöjad
                        if (newX >= 0 && newX < boardSize && newY >= 0 && newY < boardSize && !revealed[newX][newY]) {
                            queue.add(new int[]{newX, newY}); // Lägg till cellen i kön
                        }
                    }
                }
            }
        }
        return revealedCellsUpdates; // Returnera listan med uppdateringar
    }

    public boolean checkWinCondition() {
        // Kontrollera om alla icke-miner celler är avslöjade
        for (int x = 0; x < boardSize; x++) {
            for (int y = 0; y < boardSize; y++) {
                if (!mines[x][y] && !revealed[x][y]) {
                    return false; // Finns fortfarande icke-miner celler som inte är avslöjade
                }
            }
        }
        return true; // Spelaren har vunnit
    }
}
