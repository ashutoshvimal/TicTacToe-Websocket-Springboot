package com.example.TicTacToe;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class GameManager {
    private List<GameStateModel> gameStates = new ArrayList<>();
    public Map<String, GameStateModel> gameMap = new HashMap<>();

    public List<GameStateModel> findByStartedAndDisconnected(boolean started , boolean disconnected){
        List<GameStateModel> filteredGames = new ArrayList<>();

        for(GameStateModel game : gameStates){
            if(game.isStarted() == started && game.isDisconnected() == disconnected){
                filteredGames.add(game);
            }
        }
        return  filteredGames;
    }

    public GameStateModel crateGame(String player, String name){
        GameStateModel game = new GameStateModel(player, name);

        UUID uuid = UUID.randomUUID();
        String id = uuid.toString();
        game.setId(id);

//        System.out.println(game...);

        gameStates.add(game);

//        for(int i =0;i< gameStates.size();i++){
//            System.out.println(gameStates.get(i));
//        }

        gameMap.put(game.getId(), game);
        System.out.println(gameMap.size());
        for (Map.Entry<String, GameStateModel> entry : gameMap.entrySet()) {
            System.out.println("inside map print");
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }
        return game;
    }

    public GameStateModel findGameById(String id){
        System.out.println("Inside findGameById");
//        for (Map.Entry<String, GameStateModel> entry : gameMap.entrySet()) {
//            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
//        }
        for (Map.Entry<String, GameStateModel> entry : gameMap.entrySet()) {
            System.out.println("inside map print");
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }
        System.out.println(gameMap.size());
        return gameMap.get(id);
    }

}
