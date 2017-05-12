package com.dynadrop.chess.model.piece;

import com.dynadrop.chess.model.Piece;
import com.dynadrop.chess.model.Board;
import com.dynadrop.chess.websocket.bean.Position;
import com.dynadrop.chess.websocket.bean.Direction;
import java.util.ArrayList;


public class Knight implements Piece {
  private String htmlCode;
  private int color;
  private boolean moved;

  public Knight(int color) {
    this.color = color;
    this.initHtmlCode();
  }

  private void initHtmlCode() {
    if (this.color == Piece.WHITE) {
      this.htmlCode = "&#9816;";
    }else {
      this.htmlCode = "&#9822;";
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
    directions.add(new Direction(-1, -2, 1));
    directions.add(new Direction(-2, -1, 1));
    directions.add(new Direction(-2,  1, 1));
    directions.add(new Direction(-1,  2, 1));
    directions.add(new Direction( 1,  2, 1));
    directions.add(new Direction( 2,  1, 1));
    directions.add(new Direction( 2, -1, 1));
    directions.add(new Direction( 1, -2, 1));
    return directions.toArray(new Direction[0]);
  }

}
