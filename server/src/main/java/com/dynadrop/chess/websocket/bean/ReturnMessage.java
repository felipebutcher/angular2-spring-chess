package com.dynadrop.chess.websocket.bean;

import com.dynadrop.chess.websocket.bean.Movement;
import com.dynadrop.chess.model.Game;

public class ReturnMessage {
  public Game game;
  public String type;
  public int assignedPlayer;
  public Movement possibleMovements[];
  public int assignedPlayerNumber;
  public String assignedPlayerUUID;
  public String requestUUID;
}
