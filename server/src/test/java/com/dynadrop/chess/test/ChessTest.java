package com.dynadrop.chess.test;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import com.dynadrop.chess.model.Game;
import com.dynadrop.chess.model.Player;
import com.dynadrop.chess.model.Piece;
import com.dynadrop.chess.model.piece.*;
import com.dynadrop.chess.model.Movement;
import com.dynadrop.chess.model.Position;

public class ChessTest {
  Game game;
  private static int i=0;

  @Before
  public void initialize() {
    this.game = new Game("UUID_"+i++);
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
    this.testMovePiece(0,6, 0,4, true, Pawn.class, Piece.WHITE);
    this.testMovePiece(7,1, 7,3, true, Pawn.class, Piece.BLACK);
    this.testMovePiece(7,6, 7,4, true, Pawn.class, Piece.WHITE);
    this.testMovePiece(6,1, 6,3, true, Pawn.class, Piece.BLACK);
    this.testMovePiece(1,6, 1,5, true, Pawn.class, Piece.WHITE);
    this.testMovePiece(5,1, 5,3, true, Pawn.class, Piece.BLACK);
    this.testMovePiece(2,6, 3,5, false, Pawn.class, Piece.WHITE);
  }

  @Test
  public void testFoolsMate() throws Exception {
    this.testMovePiece(6,6, 6,4, true, Pawn.class, Piece.WHITE);
    this.testMovePiece(4,1, 4,2, true, Pawn.class, Piece.BLACK);
    this.testMovePiece(5,6, 5,4, true, Pawn.class, Piece.WHITE);
    this.testMovePiece(3,0, 7,4, true, Queen.class, Piece.BLACK);
    assertEquals(Game.CHECKMATE, this.game.getStatus());
  }

  @Test
  public void testCompleteMatch() throws Exception {
    this.testMovePiece(6,7, 5,5, true, Knight.class, Piece.WHITE);
    this.testMovePiece(2,1, 2,3, true, Pawn.class, Piece.BLACK);
    this.testMovePiece(3,6, 3,5, true, Pawn.class, Piece.WHITE);
    this.testMovePiece(3,0, 0,3, true, Queen.class, Piece.BLACK);
    assertEquals(Game.CHECK, this.game.getStatus());
    this.testMovePiece(1,7, 3,6, true, Knight.class, Piece.WHITE);
    assertEquals(Game.STARTED, this.game.getStatus());
    this.testMovePiece(6,1, 6,2, true, Pawn.class, Piece.BLACK);
    this.testMovePiece(3,6, 4,4, false, Knight.class, Piece.WHITE);
    this.testMovePiece(1,6, 1,4, true, Pawn.class, Piece.WHITE);
    this.testMovePiece(0,3, 1,4, true, Queen.class, Piece.BLACK);
    this.testMovePiece(5,5, 4,3, true, Knight.class, Piece.WHITE);
    this.testMovePiece(1,4, 3,6, true, Queen.class, Piece.BLACK);
    assertEquals(Game.CHECK, this.game.getStatus());
    this.testMovePiece(3,7, 3,6, true, Queen.class, Piece.WHITE);
    assertEquals(Game.STARTED, this.game.getStatus());
    this.testMovePiece(5,0, 7,2, true, Bishop.class, Piece.BLACK);
    this.testMovePiece(0,6, 0,4, true, Pawn.class, Piece.WHITE);
    this.testMovePiece(4,0, 3,0, true, King.class, Piece.BLACK);
    this.testMovePiece(0,7, 0,5, true, Rook.class, Piece.WHITE);
    this.testMovePiece(3,1, 3,3, true, Pawn.class, Piece.BLACK);
    this.testMovePiece(0,5, 2,5, true, Rook.class, Piece.WHITE);
    this.testMovePiece(6,0, 5,2, true, Knight.class, Piece.BLACK);
    this.testMovePiece(2,5, 2,3, true, Rook.class, Piece.WHITE);
    this.testMovePiece(0,1, 0,2, true, Pawn.class, Piece.BLACK);
    this.testMovePiece(3,6, 0,3, true, Queen.class, Piece.WHITE);
    assertEquals(Game.CHECK, this.game.getStatus());
    this.testMovePiece(3,0, 4,0, true, King.class, Piece.BLACK);
    assertEquals(Game.STARTED, this.game.getStatus());
    this.testMovePiece(2,3, 2,0, true, Rook.class, Piece.WHITE);
    assertEquals(Game.CHECKMATE, this.game.getStatus());
  }

  @Test
  public void testCastling() throws Exception {
    this.testMovePiece(6,7, 7,5, true);
    this.testMovePiece(7,1, 7,2, true);
    this.testMovePiece(4,6, 4,5, true);
    this.testMovePiece(0,1, 0,2, true);
    this.testMovePiece(5,7, 3,5, true);
    this.testMovePiece(1,1, 1,2, true);
    this.testMovePiece(4,7, 6,7, true);
    assertEquals(King.class, this.getPieceAt(new Position(6,7)).getClass());
    assertEquals(Rook.class, this.getPieceAt(new Position(5,7)).getClass());
  }

  @Test
  public void testPromotion() throws Exception {
    this.testMovePiece(6,6, 6,4, true);
    this.testMovePiece(5,1, 5,3, true);
    this.testMovePiece(6,4, 5,3, true);
    this.testMovePiece(6,1, 6,2, true);
    this.testMovePiece(5,3, 6,2, true);
    this.testMovePiece(6,0, 5,2, true);
    this.testMovePiece(6,2, 6,1, true);
    this.testMovePiece(0,1, 0,2, true);
    Position promotePosition = new Position(6, 0);
    this.testMovePiece(6,1, promotePosition.getX(), promotePosition.getY(), true);
    assertEquals(true, this.game.isPromotion());
    this.game.doPromote("Queen");
    assertEquals(Queen.class, this.getPieceAt(promotePosition).getClass());
  }

  @Test
  public void testBugCheckmateNotDetected() throws Exception {
    this.testMovePiece(6, 6, 6, 4, true);
    this.testMovePiece(7, 1, 7, 3, true);
    this.testMovePiece(5, 7, 6, 6, true);
    this.testMovePiece(7, 3, 6, 4, true);
    this.testMovePiece(6, 7, 5, 5, true);
    this.testMovePiece(4, 1, 4, 2, true);
    this.testMovePiece(4, 7, 6, 7, true);
    this.testMovePiece(3, 0, 7, 4, true);
    this.testMovePiece(5, 5, 3, 4, true);
    this.testMovePiece(7, 4, 7, 6, true);
    assertEquals(Game.CHECKMATE, this.game.getStatus());
  }

  private void testMovePiece(int x1, int y1, int x2, int y2, boolean expected) throws Exception {
    Movement movement = new Movement(new Position(x1, y1), new Position(x2, y2));
    assertEquals(expected, this.game.movePiece(movement));
  }

  private void testMovePiece(int x1, int y1, int x2, int y2, boolean expected, Class classObj, int color) throws Exception {
    Movement movement = new Movement(new Position(x1, y1), new Position(x2, y2));
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
