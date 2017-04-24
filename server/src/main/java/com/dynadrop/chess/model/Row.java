package com.dynadrop.chess.model;


public class Row {
  Square[] squares;

  public Row(Piece[] pieces) {
    this.squares = new Square[8];
    int i=0;
    for (Piece piece: pieces) {
      this.squares[i] = new Square(piece);
      i++;
    }
  }

  public Row() {
    this.squares = new Square[8];
    for (int i=0; i<8; i++) {
      this.squares[i] = new Square(null);
    }
  }

  public Square[] getSquares() {
    return this.squares;
  }

}
