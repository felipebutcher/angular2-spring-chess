package com.dynadrop.chess.model.piece;

import com.dynadrop.chess.model.Piece;
import com.dynadrop.chess.model.Board;
import com.dynadrop.chess.websocket.bean.Movement;


public class King implements Piece {
  private String htmlCode;
  private int color;

  public King(int color) {
    this.color = color;
    this.htmlCode = "&#9818;";
  }

  public boolean validateMovement (Movement movement, Board board) {
    System.out.println("validating movement for " + this.getClass());
    return (movement.isDestinationWithinBoard() &&
            movement.getDistance() == 1 &&
            movement.hasNoPiecesOnTheWay(board));
  }

}
