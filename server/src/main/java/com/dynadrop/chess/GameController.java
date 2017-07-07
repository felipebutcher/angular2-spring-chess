package com.dynadrop.chess;

import com.dynadrop.chess.model.Game;
import com.dynadrop.chess.model.Piece;
import com.dynadrop.chess.model.Player;
import com.dynadrop.chess.model.Movement;
import com.dynadrop.chess.model.Position;
import com.dynadrop.chess.Storage;
import org.apache.log4j.Logger;
import java.util.ArrayList;



public class GameController {
  private static final Logger logger = Logger.getLogger(GameController.class);

  public GameController() {
  }

  public void addGame(String gameUUID, String sessionId) {
    Game game = new Game(gameUUID);
    game.addWebSocketSessionId(sessionId);
    logger.info(game.getBoard());
    Storage.put(game, gameUUID);
  }

  public Game getGameByUUID(String uuid) {
    return (Game) Storage.get(uuid);
  }

  public Player joinGame(String gameUUID, String playerUUID, String sessionID) {
    Game game = this.getGameByUUID(gameUUID);
    game.addWebSocketSessionId(sessionID);
    if (game == null) {
      return null;
    }
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
    Storage.put(game, gameUUID);
    return player;
  }

  public boolean movePiece(String gameUUID, Movement movement) throws Exception {
    Game game = this.getGameByUUID(gameUUID);
    boolean moved = game.movePiece(movement);
    logger.info(game.getBoard());
    Storage.put(game, gameUUID);
    return moved;
  }

  public void doPromote(String gameUUID, String promoteTo) throws Exception {
    Game game = this.getGameByUUID(gameUUID);
    game.doPromote(promoteTo);
    logger.info(game.getBoard());
    Storage.put(game, gameUUID);
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

}
