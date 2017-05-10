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
  private boolean moved;

  public King(int color) {
    this.color = color;
    this.htmlCode = "&#9818;";
    this.moved = false;
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
    directions.addAll(this.getCastlingDirectionsIfAllowed(board, position));
    return directions.toArray(new Direction[0]);
  }

  public boolean hasMoved() {
    return this.moved;
  }

  public void setMoved(boolean moved) {
    this.moved = moved;
  }

  /*
   * it must be that king’s very first move
   * it must be that rook’s very first move
   * there cannot be any pieces between the king and rook to move
   * the king may not be in check or pass through check
   */
  private ArrayList<Direction> getCastlingDirectionsIfAllowed(Board board, Position position) {
    ArrayList<Direction> directions = new ArrayList<Direction>();
    Piece piece = board.getPieceAt(position);
    Piece pieceAtRookPositionLeft = board.getPieceAt(new Position(position.getY(), 0));
    Piece pieceAtRookPositionRight = board.getPieceAt(new Position(position.getY(), 7));
    if (pieceAtRookPositionRight != null &&
        pieceAtRookPositionRight.getClass().equals(Rook.class) &&
        ((Rook)pieceAtRookPositionRight).hasMoved() == false &&
        ((King)piece).hasMoved() == false &&
        board.getPieceAt(new Position(5, position.getY())) == null &&
        board.getPieceAt(new Position(6, position.getY())) == null ) {
      directions.add(new Direction(2, 0, 1));
    }
    if (pieceAtRookPositionLeft != null &&
        pieceAtRookPositionLeft.getClass().equals(Rook.class) &&
        ((Rook)pieceAtRookPositionLeft).hasMoved() == false &&
        ((King)piece).hasMoved() == false &&
        board.getPieceAt(new Position(3, position.getY())) == null &&
        board.getPieceAt(new Position(2, position.getY())) == null &&
        board.getPieceAt(new Position(1, position.getY())) == null) {
      directions.add(new Direction(-2, 0, 1));
    }
    return directions;
  }



}
