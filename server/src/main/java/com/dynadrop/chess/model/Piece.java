package com.dynadrop.chess.model;

import com.dynadrop.chess.websocket.bean.Movement;
import com.dynadrop.chess.model.Board;

public interface Piece {
  public final int WHITE = 0;
  public final int BLACK = 1;

  boolean validateMovement(Movement movement, Board board);

}
