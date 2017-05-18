package com.dynadrop.chess.websocket.bean;

import com.dynadrop.chess.model.Movement;

public class Message {
  public String action;
  public Movement movement;
  public String gameUUID;
  public String playerUUID;
  public String requestUUID;
  public String promoteTo;
}
