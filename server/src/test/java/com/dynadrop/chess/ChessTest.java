package com.dynadrop.chess.test;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import com.dynadrop.chess.model.Game;
import com.dynadrop.chess.model.Player;
import com.dynadrop.chess.model.piece.*;
import com.dynadrop.chess.websocket.bean.Movement;
import com.dynadrop.chess.websocket.bean.Position;

public class ChessTest {
  Game game;

  @Before
  public void initialize() {
    this.game = new Game(new Player(), "UUID");
    this.game.joinGame(new Player());
  }

  @Test
  public void testBoardInit() {
    assertEquals(this.game.getBoard().getPieceAt(new Position(0,0)).getClass(), Rook.class);
    assertEquals(this.game.getBoard().getPieceAt(new Position(1,0)).getClass(), Knight.class);
    assertEquals(this.game.getBoard().getPieceAt(new Position(2,0)).getClass(), Bishop.class);
    assertEquals(this.game.getBoard().getPieceAt(new Position(3,0)).getClass(), Queen.class);
    assertEquals(this.game.getBoard().getPieceAt(new Position(4,0)).getClass(), King.class);
    assertEquals(this.game.getBoard().getPieceAt(new Position(5,0)).getClass(), Bishop.class);
    assertEquals(this.game.getBoard().getPieceAt(new Position(6,0)).getClass(), Knight.class);
    assertEquals(this.game.getBoard().getPieceAt(new Position(7,0)).getClass(), Rook.class);
    for (int i=0; i<8; i++) {
      assertEquals(this.game.getBoard().getPieceAt(new Position(i,1)).getClass(), Pawn.class);
      assertEquals(this.game.getBoard().getPieceAt(new Position(i,6)).getClass(), Pawn.class);
    }
    assertEquals(this.game.getBoard().getPieceAt(new Position(0,7)).getClass(), Rook.class);
    assertEquals(this.game.getBoard().getPieceAt(new Position(1,7)).getClass(), Knight.class);
    assertEquals(this.game.getBoard().getPieceAt(new Position(2,7)).getClass(), Bishop.class);
    assertEquals(this.game.getBoard().getPieceAt(new Position(3,7)).getClass(), Queen.class);
    assertEquals(this.game.getBoard().getPieceAt(new Position(4,7)).getClass(), King.class);
    assertEquals(this.game.getBoard().getPieceAt(new Position(5,7)).getClass(), Bishop.class);
    assertEquals(this.game.getBoard().getPieceAt(new Position(6,7)).getClass(), Knight.class);
    assertEquals(this.game.getBoard().getPieceAt(new Position(7,7)).getClass(), Rook.class);
  }

  @Test
  public void testPawnMove() throws Exception {
    Movement movement = new Movement(new Position(0,6), new Position(0,4));
    assertEquals(true, this.game.movePiece(movement));
    movement = new Movement(new Position(7,6), new Position(7,4));
    assertEquals(true, this.game.movePiece(movement));
    movement = new Movement(new Position(1,6), new Position(1,5));
    assertEquals(true, this.game.movePiece(movement));
    movement = new Movement(new Position(2,6), new Position(3,5));
    assertEquals(false, this.game.movePiece(movement));
  }
}
