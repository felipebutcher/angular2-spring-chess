package com.dynadrop.chess.websocket.bean;

import com.dynadrop.chess.model.Movement;
import com.dynadrop.chess.model.Game;
import com.dynadrop.chess.model.Player;

public class ReturnMessage {
  public Game game;
  public String type;
  public Player assignedPlayer;
  public Movement possibleMovements[];
  public String requestUUID;
}
