package com.dynadrop.chess.model.piece;

import com.dynadrop.chess.model.Piece;
import com.dynadrop.chess.websocket.bean.Movement;


public class Pawn implements Piece {
  private String htmlCode;
  private int color;

  public Pawn(int color) {
    this.color = color;
    this.htmlCode = "&#9819;";
  }

  public boolean validateMovement (Movement movement) {
    System.out.println("TODO validateMovement for "+this.getClass());
    return true;
  }

}
