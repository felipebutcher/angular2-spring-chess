package com.dynadrop.chess.test;

import static org.junit.Assert.*;
import org.junit.Test;
import com.dynadrop.chess.model.Game;
import com.dynadrop.chess.model.Player;
import com.dynadrop.chess.model.piece.*;
import com.dynadrop.chess.websocket.bean.Movement;
import com.dynadrop.chess.websocket.bean.Position;

public class ChessTest {

  @Test
  public void testBoardInit() {
    Game game = new Game(new Player(), "UUID");
    game.joinGame(new Player());
    assertEquals(game.getBoard().getPieceAt(new Position(0,0)).getClass(), Rook.class);
    assertEquals(game.getBoard().getPieceAt(new Position(1,0)).getClass(), Knight.class);
    assertEquals(game.getBoard().getPieceAt(new Position(2,0)).getClass(), Bishop.class);
    assertEquals(game.getBoard().getPieceAt(new Position(3,0)).getClass(), Queen.class);
    assertEquals(game.getBoard().getPieceAt(new Position(4,0)).getClass(), King.class);
    assertEquals(game.getBoard().getPieceAt(new Position(5,0)).getClass(), Bishop.class);
    assertEquals(game.getBoard().getPieceAt(new Position(6,0)).getClass(), Knight.class);
    assertEquals(game.getBoard().getPieceAt(new Position(7,0)).getClass(), Rook.class);
    for (int i=0; i<8; i++) {
      assertEquals(game.getBoard().getPieceAt(new Position(i,1)).getClass(), Pawn.class);
      assertEquals(game.getBoard().getPieceAt(new Position(i,6)).getClass(), Pawn.class);
    }
    assertEquals(game.getBoard().getPieceAt(new Position(0,7)).getClass(), Rook.class);
    assertEquals(game.getBoard().getPieceAt(new Position(1,7)).getClass(), Knight.class);
    assertEquals(game.getBoard().getPieceAt(new Position(2,7)).getClass(), Bishop.class);
    assertEquals(game.getBoard().getPieceAt(new Position(3,7)).getClass(), Queen.class);
    assertEquals(game.getBoard().getPieceAt(new Position(4,7)).getClass(), King.class);
    assertEquals(game.getBoard().getPieceAt(new Position(5,7)).getClass(), Bishop.class);
    assertEquals(game.getBoard().getPieceAt(new Position(6,7)).getClass(), Knight.class);
    assertEquals(game.getBoard().getPieceAt(new Position(7,7)).getClass(), Rook.class);
  }

  @Test
  public void testPawnMove() throws Exception {
    Game game = new Game(new Player(), "UUID");
    game.joinGame(new Player());
    Movement movement = new Movement(new Position(0,6), new Position(0,5));//TODO destinado 0,4 it's not allowgin yet
    assertEquals(true, game.movePiece(movement));
    movement = new Movement(new Position(1,6), new Position(1,5));
    assertEquals(true, game.movePiece(movement));
    movement = new Movement(new Position(2,6), new Position(3,5));
    assertEquals(false, game.movePiece(movement));
  }
}
