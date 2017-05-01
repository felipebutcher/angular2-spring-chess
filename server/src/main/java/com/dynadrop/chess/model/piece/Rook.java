package com.dynadrop.chess.model.piece;

import com.dynadrop.chess.model.Piece;
import com.dynadrop.chess.websocket.bean.Movement;
import com.dynadrop.chess.websocket.bean.Direction;
import java.util.ArrayList;


public class Rook implements Piece {
  private String htmlCode;
  private int color;

  public Rook(int color) {
    this.color = color;
    this.htmlCode = "&#9820;";
  }

  public int getColor() {
    return this.color;
  }

  public Direction[] getDirections() {
    ArrayList<Direction> directions = new ArrayList<Direction>();
    directions.add(new Direction(0, -1, 7));
    directions.add(new Direction(1, 0, 7));
    directions.add(new Direction(0, 1, 7));
    directions.add(new Direction(-1, 0, 7));
    return directions.toArray(new Direction[0]);
  }

}
