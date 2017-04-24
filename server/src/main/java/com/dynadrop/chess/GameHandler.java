package com.dynadrop.chess;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.google.gson.Gson;
import com.dynadrop.chess.model.Game;
import com.dynadrop.chess.model.Board;
import com.dynadrop.chess.model.Player;
import com.dynadrop.chess.websocket.bean.Message;
import java.util.ArrayList;


@Component
public class GameHandler extends TextWebSocketHandler {

    //TODO REFACTOR ALL THIS SHIT
    WebSocketSession session;
    static ArrayList<Game> games;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
      if (games == null) {
        System.out.println("game list null, initializing");
        games = new ArrayList<Game>();
      }
      System.out.println("Connection established");
      this.session = session;
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
            System.out.println("GAMES ARRAY");
            session.sendMessage(new TextMessage(gson.toJson(game)));
          }else if ("move".equals(message.action)){
            Game game = this.getGameByUUID(message.gameUUID);
            Board board = game.getBoard();
            if (board.movePiece(message.movement)) {
              session.sendMessage(new TextMessage(gson.toJson(game)));
            }else {
              session.sendMessage(new TextMessage("{message:'invalid movement'}"));
            }
          }else if ("requestUpdate".equals(message.action)) {
            Game game = this.getGameByUUID(message.gameUUID);
            System.out.println(gson.toJson(game));
            session.sendMessage(new TextMessage(gson.toJson(game)));
          }
        }catch (Exception e) {
          e.printStackTrace();
        }
    }

    private Game getGameByUUID(String uuid) {
      System.out.println("searching game with uuid: " + uuid);
      for(Game game: this.games) {
        System.out.println("game: " + game.getUUID());
        if (game.getUUID().equals(uuid)) {
          System.out.println("FOUND GAME!!!");
          Gson gson = new Gson();
          System.out.println(gson.toJson(game));
          return game;
        }
      }
      return null;
    }

}
