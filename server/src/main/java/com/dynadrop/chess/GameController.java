package com.dynadrop.chess;

import com.dynadrop.chess.model.Game;
import com.dynadrop.chess.model.Piece;
import com.dynadrop.chess.model.Player;
import com.dynadrop.chess.websocket.bean.Movement;
import com.dynadrop.chess.websocket.bean.Position;
import org.apache.log4j.Logger;


import java.util.ArrayList;

public class GameController {
  static ArrayList<Game> games;
  final static int maxGames = 100;
  private static final Logger logger = Logger.getLogger(GameHandler.class);

  public GameController() {
    if (this.games == null) {
      logger.info("game list null, initializing");
      this.games = new ArrayList<Game>();
    }
  }

  public void addGame(String gameUUID, String sessionId) {
    this.cleanGameList();
    Game game = new Game(gameUUID);
    game.addWebSocketSessionId(sessionId);
    logger.info(game.getBoard());
    this.games.add(game);
  }

  public Game getGameByUUID(String uuid) {
    for(Game game: this.games) {
      if (game.getUUID().equals(uuid)) {
        return game;
      }
    }
    return null;
  }

  public int getNumberOfGames() {
    return this.games.size();
  }

  public Player joinGame(String gameUUID, String playerUUID, String sessionUUID) {
    Game game = this.getGameByUUID(gameUUID);
    if (game == null) {
      return null;
    }
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
    logger.info(game.getBoard());
    return moved;
  }

  public void doPromote(String gameUUID, String promoteTo) throws Exception {
    Game game = this.getGameByUUID(gameUUID);
    game.doPromote(promoteTo);
    logger.info(game.getBoard());
  }

  public Movement[] requestPossibleMovements(String gameUUID, Position position) {
    Game game = this.getGameByUUID(gameUUID);
    Piece piece = game.getBoard().getPieceAt(position);
    if (piece.getColor() == game.getTurnColor()) {
      Movement allMovements[] = game.getAllPossibleMovements(position);
      ArrayList<Movement> allPossibleMovements = new ArrayList<Movement>();
      for (Movement movement: allMovements) {
        if (!game.isOnCheckAfterMovement(movement)) {
          allPossibleMovements.add(movement);
        }
      }
      return (Movement[])allPossibleMovements.toArray(new Movement[allPossibleMovements.size()]);
    }
    return null;
  }

  public ArrayList<String> getWebSocketSessionIdsByGame(String gameUUID) {
    Game game = this.getGameByUUID(gameUUID);
    if (game == null) {
      return null;
    }
    return game.getWebSocketSessionIds();
  }

  private void cleanGameList() {
    logger.info("Cleaning game list...");
    for (int i=this.games.size()-1; i>=0; i--) {
      Game game = this.games.get(i);
      if (game.isOlderThenOneDay() || game.getStatus() == Game.CHECKMATE) {
        logger.info("Removing game: " + this.games.get(i).getUUID());
        this.games.remove(i);
      }
    }
  }

}
