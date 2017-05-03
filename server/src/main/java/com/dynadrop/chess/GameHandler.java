package com.dynadrop.chess;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.dynadrop.chess.model.Game;
import com.dynadrop.chess.model.Board;
import com.dynadrop.chess.model.Player;
import com.dynadrop.chess.model.Piece;
import com.dynadrop.chess.websocket.bean.Message;
import com.dynadrop.chess.websocket.bean.Movement;
import java.util.ArrayList;
import java.io.IOException;


@Component
public class GameHandler extends TextWebSocketHandler {

    //TODO REFACTOR ALL THIS SHIT
    static ArrayList<WebSocketSession> sessions;
    static ArrayList<Game> games;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
      if (games == null) {
        System.out.println("game list null, initializing");
        games = new ArrayList<Game>();
      }
      System.out.println("Connection established");
      if (this.sessions == null) {
        this.sessions = new ArrayList<WebSocketSession>();
      }
      this.sessions.add(session);
      //this.session = session;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage)
            throws Exception {
        try {
          System.out.println(textMessage.getPayload());//.substring(1, textMessage.getPayload().length()-1)
          Gson gson = new Gson();
          Message message = gson.fromJson(textMessage.getPayload(), Message.class);
          if ("CLOSE".equalsIgnoreCase(textMessage.getPayload())) {
            session.close();
          }else if ("newGame".equals(message.action)) {
            Player player = new Player();
            Game game = new Game(player, message.gameUUID);
            games.add(game);
            this.sendMessageToAllSessions(new TextMessage(gson.toJson(game)));
          }else if ("joinGame".equals(message.action)) {
            Game game = this.getGameByUUID(message.gameUUID);
            Player player = new Player();
            game.joinGame(player);
            this.sendMessageToAllSessions(new TextMessage(gson.toJson(game)));
          }else if ("move".equals(message.action)){
            Game game = this.getGameByUUID(message.gameUUID);
            game.movePiece(message.movement);
            this.sendMessageToAllSessions(new TextMessage(gson.toJson(game)));
          }else if ("requestUpdate".equals(message.action)) {
            Game game = this.getGameByUUID(message.gameUUID);
            this.sendMessageToAllSessions(new TextMessage(gson.toJson(game)));
          }else if ("requestPossibleMovements".equals(message.action)) {
            Game game = this.getGameByUUID(message.gameUUID);
            Piece piece = game.getBoard().getPieceAt(message.movement.getPosition1());
            if (piece.getColor() == game.getTurn()) {
              ReturnMessage returnMessage = new ReturnMessage();
              returnMessage.type = "possibleMovements";
              returnMessage.possibleMovements = game.getAllPossibleMovements(message.movement.getPosition1());
              this.sendMessageToAllSessions(new TextMessage(gson.toJson(returnMessage)));
            }
          }
        }catch (Exception e) {
          System.out.println("EXCEPTION OCURRED");
          e.printStackTrace();
        }
    }

    private void sendMessageToAllSessions(TextMessage message) throws IOException {
      //TODO send only for sessions with same game uuid
      System.out.println("Number of sessions: " + this.sessions.size());
      for (WebSocketSession session: this.sessions) {
        try {
          session.sendMessage(message);
          System.out.println("Sending message to client ");
        } catch (Exception e) {

        }
      }
    }

    private Game getGameByUUID(String uuid) {
      System.out.println("searching game with uuid: " + uuid);
      for(Game game: this.games) {
        System.out.println("game: " + game.getUUID());
        if (game.getUUID().equals(uuid)) {
          System.out.println("game found.");
          return game;
        }
      }
      return null;
    }

}

class ReturnMessage {
  String type;
  Movement possibleMovements[];
}
