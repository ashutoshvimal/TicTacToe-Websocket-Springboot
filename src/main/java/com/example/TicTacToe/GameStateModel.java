package com.example.TicTacToe;

import org.joda.time.DateTime;

import java.util.Arrays;

public class GameStateModel {
    private String Id;
    private String name;

//    @CreatedDate
//    private DateTime created;

    private boolean started;
    private boolean disconnected;
    private String player1;
    private String player2;
    private String player1Id;
    private String player2Id;
    public String[][] board = new String[3][3];
    public String startingPlayer;
    public String currentPlayer;
    public String winner;
    public boolean draw;

    public GameStateModel(String player1, String name) {
        this.name = name;
        this.player1 = player1;
        this.player2 = null;
        this.startingPlayer =player1;
        this.currentPlayer = startingPlayer;
        this.winner = null;
        this.started = false;
        this.disconnected = false;
        this.draw = false;

        resetBoard();
    }

    public String getStartingPlayer(){
        return startingPlayer.equals(player1) ? "X" : "O";
    }

    public String getId() {
        return Id;
    }
    public String getName(){
        return name;
    }

    public void setId(String id) {
        Id = id;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean isDisconnected() {
        return disconnected;
    }

    public void setDisconnected(boolean disconnected) {
        this.disconnected = disconnected;
    }

    public void join(String player2){
        if(this.player2 == null){
            this.player2 = player2;
            this.started = true;
        }
    }
    public void resetBoard(){
        for(String[] row : board) Arrays.fill(row, "");
    }

    public void makeMove(int x, int y, String player){
        //invalid move
        if(x < 0 || x >= 3) return ;
        if(y < 0 || y >= 3) return ;

        //invalid player
        if(!isValidPlayer(player)) return;

        if(started && !disconnected && !gameOver()
            && currentPlayer.equals(player) && board[x][y].equals("")){

            board[x][y] = player.equals(player1) ? "X" : "O";
            if(checkForWinner("X")) winner = player1;
            if(checkForWinner("O")) winner = player2;

            checkForDraw();
            swapCurrentPlayer();
        }
    }

    public boolean isValidPlayer(String player){
        return player.equals(player1) || player.equals(player2);
    }

    public boolean gameOver(){
        return winner != null || draw;
    }

    private boolean checkForWinner(String player) {
        for (int i = 0; i < 3; i++) {
            if (checkRow(i, player)) return true;
        }

        for (int i = 0; i < 3; i++) {
            if (checkColumn(i, player)) return true;
        }

        if (checkDiagonal(player)) return true;

        return false;
    }

    private boolean checkRow(int i, String player) {
        if (board[i][0].equals(player) && board[i][1].equals(player) && board[i][2].equals(player)) {
            return true;
        }

        return false;
    }

    private boolean checkColumn(int i, String player) {
        if (board[0][i].equals(player) && board[1][i].equals(player) && board[2][i].equals(player)) {
            return true;
        }

        return false;
    }

    private boolean checkDiagonal(String player) {
        if (board[0][0].equals(player) && board[1][1].equals(player) && board[2][2].equals(player)) {
            return true;
        }

        if (board[0][2].equals(player) && board[1][1].equals(player) && board[2][0].equals(player)) {
            return true;
        }

        return false;
    }

    public void checkForDraw(){
        draw = Arrays.stream(board).flatMap(x -> Arrays.stream(x)).noneMatch(x -> x.equals(""));
    }

    public void websocketJoin(String player, String playerId) {
        if (player.equals(player1)) {
            player1Id = playerId;
        }
        else if (player.equals(player2)) {
            player2Id = playerId;
        }
    }
    public void rematch(String player){
        if (!disconnected && gameOver() && isValidPlayer(player)) {
            winner = null;
            draw = false;
            startingPlayer = startingPlayer.equals(player1) ? player2 : player1;
            currentPlayer = startingPlayer;

            resetBoard();
        }
    }

    public void disconnect(String player){
        if(isValidPlayer(player)) setDisconnected(true);
    }

    public void disconnectById(String playerId) {
        if (isValidPlayerId(playerId)) {
            setDisconnected(true);
        }
    }

    public void swapCurrentPlayer(){
        if (currentPlayer.equals(player1)) {
            currentPlayer = player2;
        }
        else currentPlayer = player1;
    }

    public boolean isValidPlayerId(String playerId){
        return playerId.equals(player1Id) || playerId.equals(player2Id);
    }

    @Override
    public String toString() {
        return String.format(
                "GameState[id=%s, player1='%s', player2='%s']",
                Id, player1, player2);
    }
}
