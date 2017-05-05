package com.dynadrop.chess.model.piece;

import com.dynadrop.chess.model.Piece;
import com.dynadrop.chess.model.Board;
import com.dynadrop.chess.websocket.bean.Position;
import com.dynadrop.chess.websocket.bean.Movement;
import com.dynadrop.chess.websocket.bean.Direction;
import java.util.ArrayList;


public class King implements Piece {
  private String htmlCode;
  private int color;

  public King(int color) {
    this.color = color;
    this.htmlCode = "&#9818;";
  }

  public int getColor() {
    return this.color;
  }

  public Direction[] getDirections(Board board, Position position) {
    ArrayList<Direction> directions = new ArrayList<Direction>();
    directions.add(new Direction( 0, -1, 1));
    directions.add(new Direction( 1,  0, 1));
    directions.add(new Direction( 0,  1, 1));
    directions.add(new Direction(-1,  0, 1));
    directions.add(new Direction(-1, -1, 1));
    directions.add(new Direction( 1, -1, 1));
    directions.add(new Direction(-1,  1, 1));
    directions.add(new Direction( 1,  1, 1));
    return directions.toArray(new Direction[0]);
  }

}
