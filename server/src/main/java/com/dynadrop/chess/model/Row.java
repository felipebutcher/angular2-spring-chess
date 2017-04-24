package com.dynadrop.chess.model;

public class Row {
  Square[] squares;

  public Row(Piece[] pieces) {
    this.squares = new Square[8];
    int i=0;
    for (Square square : this.squares) {
      square = new Square(pieces[i]);
      i++;
    }
  }

  public Row() {
    this.squares = new Square[8];
    for (Square square : this.squares) {
      square = new Square(null);
    }
  }

  public Square[] getSquares() {
    return this.squares;
  }

}
