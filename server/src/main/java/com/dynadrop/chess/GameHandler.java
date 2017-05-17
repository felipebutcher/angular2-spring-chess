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
import org.apache.log4j.Logger;


@Component
public class GameHandler extends TextWebSocketHandler {
    static ArrayList<WebSocketSession> sessions;
    static GameController gameController;
    private static final Logger logger = Logger.getLogger(GameHandler.class);

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
      if (this.gameController == null) {
          this.gameController = new GameController();
      }
      if (this.sessions == null) {
        this.sessions = new ArrayList<WebSocketSession>();
      }
      this.addSession(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage)
            throws Exception {
        try {
          Gson gson = new Gson();
          Message message = gson.fromJson(textMessage.getPayload(), Message.class);
          logger.info("Message received: " + textMessage.getPayload());
          logger.info("Number of active games: " + this.gameController.getNumberOfGames());
          if ("CLOSE".equalsIgnoreCase(textMessage.getPayload())) {
            session.close();
          }else if ("newGame".equals(message.action)) {
            this.gameController.addGame(message.gameUUID, session.getId());
            Game game = this.gameController.getGameByUUID(message.gameUUID);
            this.sendMessageToAllSessions(message.gameUUID, new TextMessage(gson.toJson(game)));
          }else {
            ReturnMessage returnMessage = new ReturnMessage();
            if ("joinGame".equals(message.action)) {
              Player assignedPlayer = this.gameController.joinGame(message.gameUUID, message.playerUUID, session.getId());
              returnMessage.type = "joinGame";
              returnMessage.assignedPlayer = assignedPlayer;
              returnMessage.requestUUID = message.requestUUID;
              this.sendMessageToAllSessions(message.gameUUID, new TextMessage(gson.toJson(returnMessage)));
            }else if ("move".equals(message.action)){
              if (this.gameController.movePiece(message.gameUUID, message.movement)) {
                returnMessage.type = "updateBoard";
                returnMessage.game = this.gameController.getGameByUUID(message.gameUUID);
                this.sendMessageToAllSessions(message.gameUUID, new TextMessage(gson.toJson(returnMessage)));
              }
            }else if ("doPromote".equals(message.action)){
              this.gameController.doPromote(message.gameUUID, message.promoteTo);
              returnMessage.type = "updateBoard";
              returnMessage.game = this.gameController.getGameByUUID(message.gameUUID);
              this.sendMessageToAllSessions(message.gameUUID, new TextMessage(gson.toJson(returnMessage)));
            }else if ("requestUpdate".equals(message.action)) {
              returnMessage.type = "updateBoard";
              returnMessage.game = this.gameController.getGameByUUID(message.gameUUID);
              this.sendMessageToAllSessions(message.gameUUID, new TextMessage(gson.toJson(returnMessage)));
            }else if ("requestPossibleMovements".equals(message.action)) {
              Movement possibleMovements[] = this.gameController.requestPossibleMovements(message.gameUUID, message.movement.getPosition1());
              if (possibleMovements != null) {
                returnMessage.type = "possibleMovements";
                returnMessage.possibleMovements = possibleMovements;
                this.sendMessageToAllSessions(message.gameUUID, new TextMessage(gson.toJson(returnMessage)));
              }
            }
          }
        }catch (Exception e) {
          logger.error("EXCEPTION OCURRED");
          e.printStackTrace();
        }
    }

    private void sendMessageToAllSessions(String gameUUID, TextMessage message) throws IOException {
      ArrayList<String> webSocketSessionIds = this.gameController.getWebSocketSessionIdsByGame(gameUUID);
      for (WebSocketSession session: this.sessions) {
        try {
          for (String id: webSocketSessionIds) {
            if (session.getId().equals(id)) {
              session.sendMessage(message);
            }
          }
        } catch (Exception e) {

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

}
