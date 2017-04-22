package com.dynadrop.chess;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.google.gson.Gson;
import com.dynadrop.chess.model.Game;
import com.dynadrop.chess.model.Player;
import java.util.ArrayList;

@Component
public class GameHandler extends TextWebSocketHandler {

    //TODO REFACTOR ALL THIS SHIT
    WebSocketSession session;
    ArrayList<Game> games = new ArrayList<Game>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
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
              System.out.println("Game uuid:"+game.getUUID());
              games.add(game);
              session.sendMessage(new TextMessage(gson.toJson(game)));
          }else {
              System.out.println("Received:" + textMessage.getPayload());
          }
        }catch (Exception e) {
          e.printStackTrace();
        }
    }

}


class Message {
  public String action;
  public Position positionFrom;
  public Position prositionTo;
  public String gameUUID;
}

class Position {
  public int x;
  public int y;
}
