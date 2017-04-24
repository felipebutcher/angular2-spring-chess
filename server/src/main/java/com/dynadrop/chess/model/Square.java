package com.dynadrop.chess.model;

public class Square {
  Piece piece;

  public Square(Piece piece) {
    this.piece = piece;
  }

  public Piece getPiece() {
    return this.piece;
  }

  public void setPiece(Piece piece) {
    this.piece = piece;
  }

}
