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
import com.dynadrop.chess.websocket.bean.ReturnMessage;
import com.dynadrop.chess.websocket.bean.Movement;
import java.util.ArrayList;
import java.io.IOException;


@Component
public class GameHandler extends TextWebSocketHandler {

    //TODO REFACTOR ALL THIS SHIT
    //TODO delete timedout games
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
      this.addSession(session);
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
            Game game = new Game(message.gameUUID);
            game.addWebSocketSessionId(session.getId());
            games.add(game);
            this.sendMessageToAllSessions(game.getWebSocketSessionIds(), new TextMessage(gson.toJson(game)));
          }else if ("joinGame".equals(message.action)) {
            Game game = this.getGameByUUID(message.gameUUID);
            game.addWebSocketSessionId(session.getId());
            int assignedPlayerNumber = 2;//read-only
            String assignedPlayerUUID = null;
            Player player = game.getPlayerByUUID(message.playerUUID);
            if (player != null) {
              if (game.getPlayer1().getUUID().equals(message.playerUUID)) {
                assignedPlayerNumber = 0;
              }else if (game.getPlayer2().getUUID().equals(message.playerUUID)) {
                assignedPlayerNumber = 1;
              }
            }else {
              if (game.getPlayer1() == null) {
                game.setPlayer1(new Player());
                assignedPlayerUUID = game.getPlayer1().getUUID();
                assignedPlayerNumber = 0;
              }else if (game.getPlayer2() == null) {
                game.setPlayer2(new Player());
                assignedPlayerUUID = game.getPlayer2().getUUID();
                assignedPlayerNumber = 1;
              }
            }
            ReturnMessage returnMessage = new ReturnMessage();
            returnMessage.type = "joinGame";
            returnMessage.assignedPlayerNumber = assignedPlayerNumber;
            returnMessage.assignedPlayerUUID = assignedPlayerUUID;
            returnMessage.requestUUID = message.requestUUID;
            this.sendMessageToAllSessions(game.getWebSocketSessionIds(), new TextMessage(gson.toJson(returnMessage)));
          }else if ("move".equals(message.action)){
            Game game = this.getGameByUUID(message.gameUUID);
            game.addWebSocketSessionId(session.getId());
            boolean moved = game.movePiece(message.movement);
            game.getStatus();//update game status
            boolean isPromotion = game.isPromotion(message.movement);//update is promotion
            System.out.println("isPromotion:"+isPromotion);
            System.out.println(game.getBoard());
            System.out.println("MOVEMENT DONE: "+message.movement);
            ReturnMessage returnMessage = new ReturnMessage();
            returnMessage.type = "updateBoard";
            returnMessage.game = game;
            this.sendMessageToAllSessions(game.getWebSocketSessionIds(), new TextMessage(gson.toJson(returnMessage)));
          }else if ("doPromote".equals(message.action)){
            Game game = this.getGameByUUID(message.gameUUID);
            game.addWebSocketSessionId(session.getId());
            game.doPromote(message.promoteTo);
            game.getStatus();//update game status
            ReturnMessage returnMessage = new ReturnMessage();
            returnMessage.type = "updateBoard";
            returnMessage.game = game;
            this.sendMessageToAllSessions(game.getWebSocketSessionIds(), new TextMessage(gson.toJson(returnMessage)));
          }else if ("requestUpdate".equals(message.action)) {
            Game game = this.getGameByUUID(message.gameUUID);
            game.addWebSocketSessionId(session.getId());
            ReturnMessage returnMessage = new ReturnMessage();
            returnMessage.type = "updateBoard";
            returnMessage.game = game;
            this.sendMessageToAllSessions(game.getWebSocketSessionIds(), new TextMessage(gson.toJson(returnMessage)));
          }else if ("requestPossibleMovements".equals(message.action)) {
            Game game = this.getGameByUUID(message.gameUUID);
            game.addWebSocketSessionId(session.getId());
            Piece piece = game.getBoard().getPieceAt(message.movement.getPosition1());
            if (piece.getColor() == game.getTurnColor()) {
              ReturnMessage returnMessage = new ReturnMessage();
              returnMessage.type = "possibleMovements";
              returnMessage.possibleMovements = game.getAllPossibleMovements(message.movement.getPosition1());
              this.sendMessageToAllSessions(game.getWebSocketSessionIds(), new TextMessage(gson.toJson(returnMessage)));
            }
          }
        }catch (Exception e) {
          System.out.println("EXCEPTION OCURRED");
          e.printStackTrace();
        }
    }

    private void sendMessageToAllSessions(ArrayList<String> webSocketSessionIds, TextMessage message) throws IOException {
      //TODO send only for sessions with same game uuid
      System.out.println("Number of sessions: " + this.sessions.size());
      for (WebSocketSession session: this.sessions) {
        try {
          for (String id: webSocketSessionIds) {
            if (session.getId().equals(id)) {
              session.sendMessage(message);
              System.out.println("Sending message to client ");
            }
          }
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

    private void addSession(WebSocketSession newSession) {
      boolean exists = false;
      for (WebSocketSession session: this.sessions) {
        if (session.getId().equals(newSession.getId())) {
          exists = true;
        }
      }
      if (!exists) {
        this.sessions.add(newSession);
      }
    }

}
