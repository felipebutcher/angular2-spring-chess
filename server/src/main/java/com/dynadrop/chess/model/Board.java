package com.dynadrop.chess.model;

import com.dynadrop.chess.model.Square;
import com.dynadrop.chess.model.Rook;

public class Board {
  Square[][] squares;

  public Board() {
    this.squares = new Square[8][8];
    this.squares[0][0] = new Square();
    this.squares[0][0].piece = new Rook();
  }

}
