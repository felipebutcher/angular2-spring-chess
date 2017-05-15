package com.dynadrop.chess;

import com.dynadrop.chess.model.Game;
import com.dynadrop.chess.model.Piece;
import com.dynadrop.chess.model.Player;

import com.dynadrop.chess.websocket.bean.Movement;
import com.dynadrop.chess.websocket.bean.Position;


import java.util.ArrayList;

public class GameController {
  static ArrayList<Game> games;
  final static int maxGames = 100;

  public GameController() {
    if (this.games == null) {
      System.out.println("game list null, initializing");
      this.games = new ArrayList<Game>();
    }
  }
  //TODO delete timedout games

  public void addGame(String gameUUID, String sessionId) {
    Game game = new Game(gameUUID);
    game.addWebSocketSessionId(sessionId);
    System.out.println(game.getBoard());
    this.games.add(game);
  }

  public Game getGameByUUID(String uuid) {
    System.out.println("searching game with uuid: " + uuid);
    for(Game game: this.games) {
      if (game.getUUID().equals(uuid)) {
        System.out.println("game found.");
        return game;
      }
    }
    System.out.println("game not found.");
    return null;
  }

  public int getNumberOfGames() {
    return this.games.size();
  }

  public Player joinGame(String gameUUID, String playerUUID, String sessionUUID) {
    Game game = this.getGameByUUID(gameUUID);
    game.addWebSocketSessionId(sessionUUID);
    Player player = game.getPlayerByUUID(playerUUID);
    if (player != null) {
      if (game.getPlayer1().getUUID().equals(playerUUID)) {
        player.setColor(Piece.WHITE);
      }else if (game.getPlayer2().getUUID().equals(playerUUID)) {
        player.setColor(Piece.BLACK);
      }
    }else {
      if (game.getPlayer1() == null) {
        player = new Player();
        player.setColor(Piece.WHITE);
        game.setPlayer1(player);
      }else if (game.getPlayer2() == null) {
        player = new Player();
        player.setColor(Piece.BLACK);
        game.setPlayer2(player);
      }else {
        player = new Player();
        player.setColor(2);
      }
    }
    return player;
  }

  public boolean movePiece(String gameUUID, Movement movement) throws Exception {
    Game game = this.getGameByUUID(gameUUID);
    boolean moved = game.movePiece(movement);
    System.out.println(game.getBoard());
    return moved;
  }

  public void doPromote(String gameUUID, String promoteTo) throws Exception {
    Game game = this.getGameByUUID(gameUUID);
    game.doPromote(promoteTo);
    System.out.println(game.getBoard());
  }

  public Movement[] requestPossibleMovements(String gameUUID, Position position) {
    Game game = this.getGameByUUID(gameUUID);
    Piece piece = game.getBoard().getPieceAt(position);
    if (piece.getColor() == game.getTurnColor()) {
      return game.getAllPossibleMovements(position);
    }
    return null;
  }

  public ArrayList<String> getWebSocketSessionIdsByGame(String gameUUID) {
    Game game = this.getGameByUUID(gameUUID);
    return game.getWebSocketSessionIds();
  }

}
