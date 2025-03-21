package com.example.mmominesweeper.controllers;

import com.example.mmominesweeper.game.GameUpdate;
import com.example.mmominesweeper.game.PlayerMove;
import com.example.mmominesweeper.game.RevealedCellsUpdate;
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
        System.out.println("Game Reset");
        return "index";
    }
    @MessageMapping("/connect")
    public void sendCurrentGameState() {
        List<GameUpdate> currentGameState = gameService.getCurrentGameState();
        System.out.println("Sending gamestate to new connection");
        messagingTemplate.convertAndSend("/topic/new-game-state", currentGameState);
    }
    @MessageMapping("/move")
    public void handlePlayerMove(PlayerMove move) {
        int x = move.getX();
        int y = move.getY();
        int cellIndex = x + (y * gameService.getBoardSize());

        System.out.println("Player moved: (" + x + ", " + y + ") Cell index: " + cellIndex);
        
        if (gameService.isMineAt(x, y)) {
            gameService.adjustLife("-");
            if(gameService.isLifeZero()){
                System.out.println("Mine hit at: (" + x + ", " + y + ")");
                messagingTemplate.convertAndSend("/topic/game-updates", new GameUpdate(cellIndex, true, 0, false,false, gameService.getLife()));
            }else{
                System.out.println("Mine hit at: (" + x + ", " + y + ")");
                messagingTemplate.convertAndSend("/topic/game-updates", new GameUpdate(cellIndex, true, 0, false,true, gameService.getLife()));
            }
        } else {
         
            List<GameUpdate> revealedCellsUpdates = gameService.revealAdjacentCells(x, y);

            boolean isWin = gameService.checkWinCondition();

            RevealedCellsUpdate revealedCellsUpdate = new RevealedCellsUpdate(revealedCellsUpdates, isWin);
            messagingTemplate.convertAndSend("/topic/game-updates", revealedCellsUpdate);
        }
    }
}
