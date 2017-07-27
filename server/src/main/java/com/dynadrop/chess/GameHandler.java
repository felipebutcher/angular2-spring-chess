package com.dynadrop.chess;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.google.gson.Gson;
import com.dynadrop.chess.model.Game;
import com.dynadrop.chess.model.Board;
import com.dynadrop.chess.model.Player;
import com.dynadrop.chess.model.Piece;
import com.dynadrop.chess.websocket.bean.Message;
import com.dynadrop.chess.websocket.bean.ReturnMessage;
import com.dynadrop.chess.model.Movement;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.IOException;
import org.apache.log4j.Logger;


@Component
public class GameHandler extends TextWebSocketHandler {
    private static ArrayList<WebSocketSession> sessions;
    private static HashMap<String, ArrayList<String>> webSocketSessionIdsByGame;
    private static GameController gameController;
    private static Gson gson;
    private static final Logger logger = Logger.getLogger(GameHandler.class);
    private static final String NEW_GAME = "newGame";
    private static final String JOIN_GAME = "joinGame";
    private static final String MOVE = "move";
    private static final String PROMOTE = "doPromote";
    private static final String REQUEST_UPDATE = "requestUpdate";
    private static final String REQUEST_POSSIBLE_MOVEMENTS = "requestPossibleMovements";

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
      if (this.gameController == null) {
        this.gameController = new GameController();
      }
      if (this.sessions == null) {
        this.sessions = new ArrayList<WebSocketSession>();
      }
      if (gson == null) {
        gson = new Gson();
      }
      this.addSession(session);
      if (this.webSocketSessionIdsByGame == null) {
        this.webSocketSessionIdsByGame = new HashMap<String, ArrayList<String>>();
      }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
      if ("CLOSE".equalsIgnoreCase(textMessage.getPayload())) {
        session.close();
      }
      try {
        Message message = this.gson.fromJson(textMessage.getPayload(), Message.class);
        logger.info("Message received: " + textMessage.getPayload());
        switch(message.action) {
          case NEW_GAME: this.newGame(message, session); break;
          case JOIN_GAME: this.joinGame(message, session); break;
          case MOVE: this.move(message); break;
          case PROMOTE: this.promote(message); break;
          case REQUEST_UPDATE: this.requestUpdate(message); break;
          case REQUEST_POSSIBLE_MOVEMENTS: this.requestPossibleMovements(message); break;
        }
      }catch (Exception e) {
        logger.error("EXCEPTION OCURRED");
        logger.error( "failed! ", e );
      }
    }

    private void sendMessage(String gameUUID, TextMessage message) throws IOException {
      ArrayList<String> webSocketSessionIds = this.getWebSocketSessionIdsByGame(gameUUID);
      for (WebSocketSession session: this.sessions) {
        for (String id: webSocketSessionIds) {
          try {
            if (session.getId().equals(id)) {
              System.out.println("sending message to: "+id);
              session.sendMessage(message);
            }
          } catch (Exception e) { }
        }
      }
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

    private void newGame(Message message, WebSocketSession session) throws IOException {
      this.gameController.addGame(message.gameUUID);
      this.addWebSocketSessionIdToGame(message.gameUUID, session.getId());
      Game game = this.gameController.getGameByUUID(message.gameUUID);
      this.sendMessage(message.gameUUID, new TextMessage(this.gson.toJson(game)));
    }

    private void joinGame(Message message, WebSocketSession session) throws IOException {
      Player assignedPlayer = this.gameController.joinGame(message.gameUUID, message.playerUUID);
      this.addWebSocketSessionIdToGame(message.gameUUID, session.getId());
      ReturnMessage returnMessage = new ReturnMessage();
      returnMessage.type = "joinGame";
      returnMessage.assignedPlayer = assignedPlayer;
      returnMessage.requestUUID = message.requestUUID;
      this.sendMessage(message.gameUUID, new TextMessage(this.gson.toJson(returnMessage)));
    }

    private void addWebSocketSessionIdToGame(String gameUUID, String sessionId) {
      if (this.webSocketSessionIdsByGame.get(gameUUID) == null) {
        this.webSocketSessionIdsByGame.put(gameUUID, new ArrayList<String>());
      }
      if (!this.webSocketSessionIdsByGame.get(gameUUID).contains(sessionId)) {
        this.webSocketSessionIdsByGame.get(gameUUID).add(sessionId);
      }
    }

    public ArrayList<String> getWebSocketSessionIdsByGame(String gameUUID) {
      Game game = this.gameController.getGameByUUID(gameUUID);
      if (game == null) {
        return null;
      }
      return this.webSocketSessionIdsByGame.get(gameUUID);
    }

    private void move(Message message) throws IOException, Exception {
      if (this.gameController.movePiece(message.gameUUID, message.movement)) {
        ReturnMessage returnMessage = new ReturnMessage();
        returnMessage.type = "updateBoard";
        returnMessage.game = this.gameController.getGameByUUID(message.gameUUID);
        this.sendMessage(message.gameUUID, new TextMessage(this.gson.toJson(returnMessage)));
      }
    }

    private void promote(Message message) throws Exception {
      this.gameController.doPromote(message.gameUUID, message.promoteTo);
      ReturnMessage returnMessage = new ReturnMessage();
      returnMessage.type = "updateBoard";
      returnMessage.game = this.gameController.getGameByUUID(message.gameUUID);
      this.sendMessage(message.gameUUID, new TextMessage(this.gson.toJson(returnMessage)));
    }

    private void requestUpdate(Message message) throws IOException {
      ReturnMessage returnMessage = new ReturnMessage();
      returnMessage.type = "updateBoard";
      returnMessage.game = this.gameController.getGameByUUID(message.gameUUID);
      this.sendMessage(message.gameUUID, new TextMessage(this.gson.toJson(returnMessage)));
    }

    private void requestPossibleMovements(Message message) throws IOException {
      Movement possibleMovements[] = this.gameController.requestPossibleMovements(message.gameUUID, message.movement.getPosition1());
      if (possibleMovements != null) {
        ReturnMessage returnMessage = new ReturnMessage();
        returnMessage.type = "possibleMovements";
        returnMessage.possibleMovements = possibleMovements;
        this.sendMessage(message.gameUUID, new TextMessage(this.gson.toJson(returnMessage)));
      }
    }

}
