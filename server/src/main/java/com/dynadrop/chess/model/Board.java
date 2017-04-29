package com.dynadrop.chess.model;

import com.dynadrop.chess.model.Square;
import com.dynadrop.chess.model.piece.*;
import com.dynadrop.chess.model.Row;
import com.dynadrop.chess.websocket.bean.Movement;
import com.dynadrop.chess.websocket.bean.Position;

public class Board {
  Row[] rows;

  public Board() {
    this.initBoard();
  }

  private void initBoard() {
    System.out.println("Board.initBoard()");
    this.rows = new Row[8];
    this.rows[0] = new Row(new Piece[]{
      new Rook(Piece.BLACK),
      new Knight(Piece.BLACK),
      new Bishop(Piece.BLACK),
      new Queen(Piece.BLACK),
      new King(Piece.BLACK),
      new Bishop(Piece.BLACK),
      new Knight(Piece.BLACK),
      new Rook(Piece.BLACK)
    });
    this.rows[1] = new Row(new Piece[]{
      new Pawn(Piece.BLACK),
      new Pawn(Piece.BLACK),
      new Pawn(Piece.BLACK),
      new Pawn(Piece.BLACK),
      new Pawn(Piece.BLACK),
      new Pawn(Piece.BLACK),
      new Pawn(Piece.BLACK),
      new Pawn(Piece.BLACK)
    });
    for (int i=2; i<6; i++) {
      this.rows[i] = new Row();
    }
    this.rows[6] = new Row(new Piece[]{
      new Pawn(Piece.WHITE),
      new Pawn(Piece.WHITE),
      new Pawn(Piece.WHITE),
      new Pawn(Piece.WHITE),
      new Pawn(Piece.WHITE),
      new Pawn(Piece.WHITE),
      new Pawn(Piece.WHITE),
      new Pawn(Piece.WHITE)
    });
    this.rows[7] = new Row(new Piece[]{
      new Rook(Piece.WHITE),
      new Knight(Piece.WHITE),
      new Bishop(Piece.WHITE),
      new Queen(Piece.WHITE),
      new King(Piece.WHITE),
      new Bishop(Piece.WHITE),
      new Knight(Piece.WHITE),
      new Rook(Piece.WHITE)
    });
  }

  public Row[] getRows() {
    return this.rows;
  }

  public Piece getPieceAt(Position position) {
    return this.rows[position.getY()].getSquares()[position.getX()].getPiece();
  }

  public void setPieceAt(Position position, Piece piece) {
    this.rows[position.getY()].getSquares()[position.getX()].setPiece(piece);
  }

}
