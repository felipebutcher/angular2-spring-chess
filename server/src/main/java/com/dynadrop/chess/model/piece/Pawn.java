package com.dynadrop.chess.model.piece;

import com.dynadrop.chess.model.Piece;
import com.dynadrop.chess.websocket.bean.Movement;
import com.dynadrop.chess.websocket.bean.Direction;
import java.util.ArrayList;


public class Pawn implements Piece {
  private String htmlCode;
  private int color;

  public Pawn(int color) {
    this.color = color;
    this.htmlCode = "&#9823;";
  }

  public int getColor() {
    return this.color;
  }

  public Direction[] getDirections() {
    ArrayList<Direction> directions = new ArrayList<Direction>();
    directions.add(new Direction(0, -1, 1));//move straight
    //TODO allow 2 moves on the first take
    return directions.toArray(new Direction[0]);
  }

}
