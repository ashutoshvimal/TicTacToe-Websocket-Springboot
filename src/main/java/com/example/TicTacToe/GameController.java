package com.example.TicTacToe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class GameController {
    @Autowired
    private GameManager gameManager;

//    public GameController(GameManager gameManager) {
//        this.gameManager = gameManager;
//    }
    @MessageMapping("/move/{id}")
    @SendTo("/app/gamestate/{id}")
    public GameStateModel handleMove(@DestinationVariable String id, PlayerMoveMessage move){
        System.out.println("Inside gameState");
        GameStateModel game = gameManager.findGameById(id);
        game.makeMove(move.getX(), move.getY(), move.getPlayer());
        return game;
    }
    @MessageMapping("/join/{id}")
//    @SendTo("/topic/gamestate/{id}")
    public GameStateModel join(SimpMessageHeaderAccessor headerAccessor, @DestinationVariable String id, PlayerJoinMessage message) {
        System.out.println("Inside GameManager join");
        System.out.println(id);
        GameStateModel game = gameManager.findGameById(id);


        game.websocketJoin(message.getPlayer(), headerAccessor.getSessionId());

        return game;
    }

}
