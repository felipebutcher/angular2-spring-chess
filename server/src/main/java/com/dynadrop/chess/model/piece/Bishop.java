package com.dynadrop.chess.model.piece;

import com.dynadrop.chess.model.Piece;
import com.dynadrop.chess.model.Board;
import com.dynadrop.chess.model.Position;
import com.dynadrop.chess.model.Movement;
import com.dynadrop.chess.model.Direction;
import java.util.ArrayList;
import java.io.Serializable;


public class Bishop implements Piece, Serializable {
  private String htmlCode;
  private int color;
  private boolean moved;

  public Bishop(int color) {
    this.color = color;
    this.initHtmlCode();
  }

  private void initHtmlCode() {
    if (this.color == Piece.WHITE) {
      this.htmlCode = "&#9815;";
    }else {
      this.htmlCode = "&#9821;";
    }
  }

  public int getColor() {
    return this.color;
  }

  public boolean hasMoved() {
    return this.moved;
  }

  public void setMoved(boolean moved) {
    this.moved = moved;
  }

  public Direction[] getDirections(Board board, Position position) {
    ArrayList<Direction> directions = new ArrayList<Direction>();
    directions.add(new Direction(-1,  1, 7));
    directions.add(new Direction(-1, -1, 7));
    directions.add(new Direction( 1, -1, 7));
    directions.add(new Direction( 1,  1, 7));
    return directions.toArray(new Direction[0]);
  }

}
