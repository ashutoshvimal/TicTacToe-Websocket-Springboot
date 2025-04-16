package com.example.TicTacToe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin
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
    }


    // Join
    @RequestMapping(method = RequestMethod.POST, value = "/app/game/join", params = {"id", "player"})
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

    // Disconnect
    @RequestMapping(method = RequestMethod.POST, value = "/app/game/disconnect", params = {"id", "player", "disconnect"})
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


    // Rematch
    @RequestMapping(method = RequestMethod.POST, value = "/app/game/rematch", params = {"id", "player", "rematch"})
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

