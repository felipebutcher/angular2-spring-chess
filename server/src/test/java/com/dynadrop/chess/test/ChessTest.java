package com.dynadrop.chess.test;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import com.dynadrop.chess.model.Game;
import com.dynadrop.chess.model.Player;
import com.dynadrop.chess.model.Piece;
import com.dynadrop.chess.model.piece.*;
import com.dynadrop.chess.websocket.bean.Movement;
import com.dynadrop.chess.websocket.bean.Position;

public class ChessTest {
  Game game;

  @Before
  public void initialize() {
    this.game = new Game("UUID");
    this.game.setPlayer1(new Player());
    this.game.setPlayer2(new Player());
  }

  @Test
  public void testBoardInit() {
    assertEquals(this.getPieceAt(new Position(0,0)).getClass(), Rook.class);
    assertEquals(this.getPieceAt(new Position(1,0)).getClass(), Knight.class);
    assertEquals(this.getPieceAt(new Position(2,0)).getClass(), Bishop.class);
    assertEquals(this.getPieceAt(new Position(3,0)).getClass(), Queen.class);
    assertEquals(this.getPieceAt(new Position(4,0)).getClass(), King.class);
    assertEquals(this.getPieceAt(new Position(5,0)).getClass(), Bishop.class);
    assertEquals(this.getPieceAt(new Position(6,0)).getClass(), Knight.class);
    assertEquals(this.getPieceAt(new Position(7,0)).getClass(), Rook.class);
    for (int i=0; i<8; i++) {
      assertEquals(this.getPieceAt(new Position(i,1)).getClass(), Pawn.class);
      assertEquals(this.getPieceAt(new Position(i,6)).getClass(), Pawn.class);
    }
    assertEquals(this.getPieceAt(new Position(0,7)).getClass(), Rook.class);
    assertEquals(this.getPieceAt(new Position(1,7)).getClass(), Knight.class);
    assertEquals(this.getPieceAt(new Position(2,7)).getClass(), Bishop.class);
    assertEquals(this.getPieceAt(new Position(3,7)).getClass(), Queen.class);
    assertEquals(this.getPieceAt(new Position(4,7)).getClass(), King.class);
    assertEquals(this.getPieceAt(new Position(5,7)).getClass(), Bishop.class);
    assertEquals(this.getPieceAt(new Position(6,7)).getClass(), Knight.class);
    assertEquals(this.getPieceAt(new Position(7,7)).getClass(), Rook.class);
  }

  @Test
  public void testPawnMove() throws Exception {
    this.testMovePiece(new Movement(new Position(0,6), new Position(0,4)), true, Pawn.class, Piece.WHITE);
    this.testMovePiece(new Movement(new Position(7,1), new Position(7,3)), true, Pawn.class, Piece.BLACK);
    this.testMovePiece(new Movement(new Position(7,6), new Position(7,4)), true, Pawn.class, Piece.WHITE);
    this.testMovePiece(new Movement(new Position(6,1), new Position(6,3)), true, Pawn.class, Piece.BLACK);
    this.testMovePiece(new Movement(new Position(1,6), new Position(1,5)), true, Pawn.class, Piece.WHITE);
    this.testMovePiece(new Movement(new Position(5,1), new Position(5,3)), true, Pawn.class, Piece.BLACK);
    this.testMovePiece(new Movement(new Position(2,6), new Position(3,5)), false, Pawn.class, Piece.WHITE);
  }

  @Test
  public void testFoolsMate() throws Exception {
    this.testMovePiece(new Movement(new Position(6,6), new Position(6,4)), true, Pawn.class, Piece.WHITE);
    this.testMovePiece(new Movement(new Position(4,1), new Position(4,2)), true, Pawn.class, Piece.BLACK);
    this.testMovePiece(new Movement(new Position(5,6), new Position(5,4)), true, Pawn.class, Piece.WHITE);
    this.testMovePiece(new Movement(new Position(3,0), new Position(7,4)), true, Queen.class, Piece.BLACK);
    assertEquals(Game.CHECKMATE, this.game.getStatus());
  }

  @Test
  public void testCompleteMatch() throws Exception {
    this.testMovePiece(new Movement(new Position(6,7), new Position(5,5)), true, Knight.class, Piece.WHITE);
    this.testMovePiece(new Movement(new Position(2,1), new Position(2,3)), true, Pawn.class, Piece.BLACK);
    this.testMovePiece(new Movement(new Position(3,6), new Position(3,5)), true, Pawn.class, Piece.WHITE);
    this.testMovePiece(new Movement(new Position(3,0), new Position(0,3)), true, Queen.class, Piece.BLACK);
    assertEquals(Game.CHECK, this.game.getStatus());
    this.testMovePiece(new Movement(new Position(1,7), new Position(3,6)), true, Knight.class, Piece.WHITE);
    assertEquals(Game.STARTED, this.game.getStatus());
    this.testMovePiece(new Movement(new Position(6,1), new Position(6,2)), true, Pawn.class, Piece.BLACK);
    this.testMovePiece(new Movement(new Position(3,6), new Position(4,4)), false, Knight.class, Piece.WHITE);
    this.testMovePiece(new Movement(new Position(1,6), new Position(1,4)), true, Pawn.class, Piece.WHITE);
    this.testMovePiece(new Movement(new Position(0,3), new Position(1,4)), true, Queen.class, Piece.BLACK);
    this.testMovePiece(new Movement(new Position(5,5), new Position(4,3)), true, Knight.class, Piece.WHITE);
    this.testMovePiece(new Movement(new Position(1,4), new Position(3,6)), true, Queen.class, Piece.BLACK);
    assertEquals(Game.CHECK, this.game.getStatus());
    this.testMovePiece(new Movement(new Position(3,7), new Position(3,6)), true, Queen.class, Piece.WHITE);
    assertEquals(Game.STARTED, this.game.getStatus());
    this.testMovePiece(new Movement(new Position(5,0), new Position(7,2)), true, Bishop.class, Piece.BLACK);
    this.testMovePiece(new Movement(new Position(0,6), new Position(0,4)), true, Pawn.class, Piece.WHITE);
    this.testMovePiece(new Movement(new Position(4,0), new Position(3,0)), true, King.class, Piece.BLACK);
    this.testMovePiece(new Movement(new Position(0,7), new Position(0,5)), true, Rook.class, Piece.WHITE);
    this.testMovePiece(new Movement(new Position(3,1), new Position(3,3)), true, Pawn.class, Piece.BLACK);
    this.testMovePiece(new Movement(new Position(0,5), new Position(2,5)), true, Rook.class, Piece.WHITE);
    this.testMovePiece(new Movement(new Position(6,0), new Position(5,2)), true, Knight.class, Piece.BLACK);
    this.testMovePiece(new Movement(new Position(2,5), new Position(2,3)), true, Rook.class, Piece.WHITE);
    this.testMovePiece(new Movement(new Position(0,1), new Position(0,2)), true, Pawn.class, Piece.BLACK);
    this.testMovePiece(new Movement(new Position(3,6), new Position(0,3)), true, Queen.class, Piece.WHITE);
    assertEquals(Game.CHECK, this.game.getStatus());
    this.testMovePiece(new Movement(new Position(3,0), new Position(4,0)), true, King.class, Piece.BLACK);
    assertEquals(Game.STARTED, this.game.getStatus());
    this.testMovePiece(new Movement(new Position(2,3), new Position(2,0)), true, Rook.class, Piece.WHITE);
    assertEquals(Game.CHECKMATE, this.game.getStatus());
  }

  private void testMovePiece(Movement movement, boolean expected, Class classObj, int color) throws Exception {
    assertEquals(expected, this.game.movePiece(movement));
    if (this.getPieceAt(movement.getPosition2()) != null) {
      assertEquals(classObj, this.getPieceAt(movement.getPosition2()).getClass());
      assertEquals(color, this.getPieceAt(movement.getPosition2()).getColor());
    }
  }

  private Piece getPieceAt(Position position) {
    return this.game.getBoard().getPieceAt(position);
  }
}
