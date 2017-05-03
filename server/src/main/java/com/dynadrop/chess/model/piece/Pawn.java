package com.dynadrop.chess.model.piece;

import com.dynadrop.chess.model.Piece;
import com.dynadrop.chess.model.Board;
import com.dynadrop.chess.websocket.bean.Movement;
import com.dynadrop.chess.websocket.bean.Direction;
import com.dynadrop.chess.websocket.bean.Position;
import java.util.ArrayList;


public class Pawn implements Piece {
  private String htmlCode;
  private int color;
  private Board board;
  private Position position;

  public Pawn(int color) {
    this.color = color;
    this.htmlCode = "&#9823;";
  }

  public int getColor() {
    return this.color;
  }

  public Direction[] getDirections() {
    ArrayList<Direction> directions = new ArrayList<Direction>();
    int delta = 1;
    System.out.println("PIECE COLOR: "+this.color);
    if (this.color == Piece.BLACK) {
      delta = -1;
    }
    if (this.position.getY() == 6 || this.position.getY() == 1) {
      directions.add(new Direction(0, -1*delta, 2));//move straight two squares
    }else if (this.board.getPieceAt(new Position(this.position.getX(), (this.position.getY()-1)*delta)) == null) {
      directions.add(new Direction(0, -1*delta, 1));//move straight
    }
    Position positionTo = new Position(this.position.getX()-1, (this.position.getY()-(1*delta)));
    if (positionTo.isWithinBoard() && this.board.getPieceAt(positionTo) != null
        && this.board.getPieceAt(positionTo).getColor() != this.color) {
      directions.add(new Direction(-1, -1*delta, 1));//move diagonal left when 'killing' enemy
    }
    positionTo = new Position(this.position.getX()+1, (this.position.getY()-(1*delta)));
    if (positionTo.isWithinBoard() && this.board.getPieceAt(positionTo) != null
        && this.board.getPieceAt(positionTo).getColor() != this.color) {
      directions.add(new Direction(1, -1*delta, 1));//move diagonal right when 'killing' enemy
    }
    //TODO board should not be here, it was causing error on gson.toJson
    this.board = null;
    this.position = null;
    return directions.toArray(new Direction[0]);
  }

  public void setBoardAndPosition(Board board, Position position) {
    this.board = board;
    this.position = position;
  }

}
