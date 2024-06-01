package com.example.TicTacToe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class ServerController {

    @Autowired
    private SimpMessagingTemplate template;
    @Autowired
    private GameManager gameManager = new GameManager();
    @RequestMapping(method = RequestMethod.GET, value = "/app/games")
    public List<AvailableGame> getGame(){
        List<AvailableGame> availableGames = new ArrayList<>();
        List<GameStateModel> games = gameManager.findByStartedAndDisconnected(false, false);

        for(GameStateModel game : games){
            availableGames.add(new AvailableGame(game.getId(), game.getName()));
            System.out.println(game.getId());
            System.out.println(game.getName());
        }

        return availableGames;
    }

    @RequestMapping(method = RequestMethod.POST, value = "app/game")
    public GameStateModel createGame(@RequestParam(value = "player") String player,
                                     @RequestParam(value = "name", defaultValue = "A TicTacToe Game") String name){
        System.out.println("Inside Server controller create game");

       return gameManager.crateGame(player, name);

       //can be moved to different method
//       UUID uuid = UUID.randomUUID();
//       String id = uuid.toString();
//       game.setId(id);
//
//       return game;
    }
/*
    @RequestMapping(method = RequestMethod.PATCH, value = "/app/game", params = {"id", "player"})
    public ResponseEntity<GameStateModel> joinGame(@RequestParam(value = "id") String id,
                                   @RequestParam(value = "player") String player){
        GameStateModel game = gameManager.findGameById(id);

        if (game == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        if(!game.isStarted() && !game.isDisconnected()){
            game.join(player);
            updateGameState(id, game);

            return ResponseEntity.ok(game);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }
    */

    //CHATGPT suggested code


//    @RequestMapping(method = RequestMethod.PATCH, value = "/app/game", params = {"id", "player"})
//public ResponseEntity<GameStateModel> joinGame(@RequestParam(value = "id") String id,
//                                               @RequestParam(value = "player") String player) {
//        System.out.println("Inside JOIN game");
//    try {
//        GameStateModel game = gameManager.findGameById(id);
//        if (game == null) {
//            System.out.println("game is null");
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//
//        }
//
//        if (!game.isStarted() && !game.isDisconnected()) {
//            game.join(player);
//            updateGameState(id, game);
//            return ResponseEntity.ok(game);
//        }
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
//    } catch (Exception e) {
//        e.printStackTrace();  // Log the exception for debugging
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//    }
//}

    // Join
    @RequestMapping(method = RequestMethod.PATCH, value = "/app/game", params = {"id", "player"})
    public GameStateModel joinGame(@RequestParam(value = "id") String id,
                                   @RequestParam(value = "player") String player) {

        GameStateModel game = gameManager.findGameById(id);

        if (!game.isStarted() && !game.isDisconnected()) {
            game.join(player);

            updateGameState(id, game);

            return game;
        }

        // return null if third player is trying to join or player left
        return null;
    }

    //copied
    // Disconnect
    @RequestMapping(method = RequestMethod.PATCH, value = "/app/game", params = {"id", "player", "disconnect"})
    public GameStateModel disconnectGame(
            @RequestParam(value = "id") String id,
            @RequestParam(value = "player") String player,
            @RequestParam(value = "disconnect") boolean disconnect) {




            GameStateModel game = gameManager.findGameById(id);
            if (disconnect) {
                game.disconnect(player);

                updateGameState(id, game);

                return game;
            }



        return null;
    }


    //copied
    // Rematch
    @RequestMapping(method = RequestMethod.PATCH, value = "/app/game", params = {"id", "player", "rematch"})
    public GameStateModel rematchGame(
            @RequestParam(value = "id") String id,
            @RequestParam(value = "player") String player,
            @RequestParam(value = "rematch") boolean rematch) {

        GameStateModel game = gameManager.findGameById(id);

        if (rematch) {
            game.rematch(player);

            updateGameState(id, game);

            return game;
        }

        return null;
    }

    public void updateGameState(String id, GameStateModel game){
        template.convertAndSend("/app/gamestate/"+id, game);
    }
}

