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
        this.gameService.initializeGame(); 
    }

    @PostMapping("/reset")
    public String resetGame() {
        gameService.resetGame();
        messagingTemplate.convertAndSend("/topic/game-updates", new GameUpdate(-1, false, 0, false, true));
        return "index";
    }

    @MessageMapping("/move")
    public void handlePlayerMove(PlayerMove move) {
        int x = move.getX();
        int y = move.getY();
        int cellIndex = x + (y * gameService.getBoardSize());

        System.out.println("Player moved: (" + x + ", " + y + ") Cell index: " + cellIndex);
        
        if (gameService.isMineAt(x, y)) {
            System.out.println("Mine hit at: (" + x + ", " + y + ")");
            messagingTemplate.convertAndSend("/topic/game-updates", new GameUpdate(cellIndex, true, 0, false,false));
        } else {
         
            List<GameUpdate> revealedCellsUpdates = gameService.revealAdjacentCells(x, y);

            boolean isWin = gameService.checkWinCondition();

            RevealedCellsUpdate revealedCellsUpdate = new RevealedCellsUpdate(revealedCellsUpdates, isWin);
            messagingTemplate.convertAndSend("/topic/game-updates", revealedCellsUpdate);
        }
    }
}
