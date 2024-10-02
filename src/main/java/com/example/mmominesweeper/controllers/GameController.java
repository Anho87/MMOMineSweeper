package com.example.mmominesweeper.controllers;

import com.example.mmominesweeper.models.GameUpdate;
import com.example.mmominesweeper.models.PlayerMove;
import com.example.mmominesweeper.models.RevealedCellsUpdate;
import com.example.mmominesweeper.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class GameController {

    private final SimpMessagingTemplate messagingTemplate;
    private final GameService gameService;

    @Autowired
    public GameController(SimpMessagingTemplate messagingTemplate, GameService gameService) {
        this.messagingTemplate = messagingTemplate;
        this.gameService = gameService;
        this.gameService.initializeGame(); // Initiera spelet här
    }

    @PostMapping("/reset")
    public void resetGame() {
        gameService.resetGame();
        messagingTemplate.convertAndSend("/topic/game-updates", new GameUpdate(-1, false, 0, false));
    }

    @MessageMapping("/move")
    public void handlePlayerMove(PlayerMove move) {
        int x = move.getX();
        int y = move.getY();
        int cellIndex = x + (y * gameService.getBoardSize());

        System.out.println("Player moved: (" + x + ", " + y + ") Cell index: " + cellIndex); // Logga spelarens drag

        // Kontrollera om spelaren klickade på en mina
        if (gameService.isMineAt(x, y)) {
            System.out.println("Mine hit at: (" + x + ", " + y + ")");
            // Skicka uppdatering för att visa att en mina har träffats
            messagingTemplate.convertAndSend("/topic/game-updates", new GameUpdate(cellIndex, true, 0, false));
        } else {
            // Avslöja alla celler som ska avslöjas
            List<GameUpdate> revealedCellsUpdates = gameService.revealAdjacentCells(x, y);

            // Kontrollera om spelet har vunnits
            boolean isWin = gameService.checkWinCondition();

            // Skapa en RevealedCellsUpdate och skicka den med alla avslöjade celler och vinststatus
            RevealedCellsUpdate revealedCellsUpdate = new RevealedCellsUpdate(revealedCellsUpdates, isWin);
            messagingTemplate.convertAndSend("/topic/game-updates", revealedCellsUpdate);
        }
    }


}
