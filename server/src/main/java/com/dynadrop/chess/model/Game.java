package com.dynadrop.chess.model;

import com.dynadrop.chess.model.Player;
import com.dynadrop.chess.model.Board;
import com.dynadrop.chess.model.Player;
import com.dynadrop.chess.model.piece.*;
import com.dynadrop.chess.websocket.bean.Movement;
import com.dynadrop.chess.websocket.bean.Position;
import com.dynadrop.chess.websocket.bean.Direction;
import java.util.ArrayList;

public class Game {
  private Board board;
  private Player player1;
  private Player player2;
  private String uuid;
  private int status;
  private int playerTurn;

  public final int STARTED = 0;
  public final int CHECK = 1;
  public final int MATE = 2;
  public final int PLAYER1 = 0;
  public final int PLAYER2 = 1;


  public Game(Player player1, String uuid) {
    this.board = new Board();
    this.player1 = player1;
    this.uuid = uuid;
    this.status = STARTED;
  }

  public Board getBoard() {
    return this.board;
  }

  public Player getPlayer1() {
    return this.player1;
  }

  public Player getPlayer2() {
    return this.player2;
  }

  public void joinGame(Player player2) {
    this.player2 = player2;
  }

  public String getUUID() {
    return uuid;
  }

  public int getStatus() {
    return this.status;
  }

  public boolean movePiece(Movement movement) throws Exception{
    Movement possibleMovements[] = this.getAllPossibleMovements(movement.getPosition1());
    System.out.println("Requested movement: "+movement);
    for(Movement possibleMovement: possibleMovements) {
      System.out.println("Possible movement: "+possibleMovement);
      if (movement.equals(possibleMovement)) {
        Piece piece = this.board.getPieceAt(movement.getPosition1());
        this.board.setPieceAt(movement.getPosition1(), null);
        this.board.setPieceAt(movement.getPosition2(), piece);
        this.updateStatus(movement.getPosition2());
        System.out.println(this.board);
        System.out.println("Movement is VALID");
        return true;
      }
    }
    return false;
  }

  public Movement[] getAllPossibleMovements(Position position) {
    ArrayList<Movement> movements = new ArrayList<Movement>();
    Piece piece = this.board.getPieceAt(position);
    if (piece.getClass().equals(Pawn.class)) {
      ((Pawn)piece).setBoardAndPosition(this.board, position);
    }
    Direction directions[] = piece.getDirections();
    //System.out.println("Directions for "+piece.getClass());
    for (Direction direction: directions) {
      //System.out.println("Direction: "+direction.getX()+","+direction.getY()+" limit:"+direction.getLimit());
      Position positionFrom = position;
      Position positionTo = new Position(positionFrom.getX()+direction.getX(), positionFrom.getY()+direction.getY());
      int i=0;
      while (this.canMoveTo(piece, positionTo) && i<direction.getLimit()) {
        movements.add(new Movement(position, positionTo));
        positionFrom = positionTo;
        positionTo = new Position(positionFrom.getX()+direction.getX(), positionFrom.getY()+direction.getY());
        i++;
      }
    }
    return movements.toArray(new Movement[0]);
  }

  private boolean canMoveTo(Piece piece, Position position) {
    if (!position.isWithinBoard()) {
      return false;
    }
    Piece pieceAtDestination = getPieceAt(position);
    if (pieceAtDestination != null) {
      System.out.println("pieceAtDestination.color:"+pieceAtDestination.getColor());
    }
    if (pieceAtDestination == null || piece.getColor()!=pieceAtDestination.getColor()) {
      //System.out.println(piece.getClass()+" can move to "+position.getX()+","+position.getY()+"");
      return true;
    } else {
      //System.out.println(piece.getClass()+" can NOT move to "+position.getX()+","+position.getY()+"");
      return false;
    }
  }

  public Position[] getAllPiecesPositions(int color) {
    ArrayList<Position> positions = new ArrayList<Position>();
    for (int x=0; x<7; x++) {
      for (int y=0; y<7; y++) {
        Piece piece = this.board.getPieceAt(new Position(x, y));
        if (piece != null && piece.getColor() == color) {
          positions.add(new Position(x, y));
        }
      }
    }
    return positions.toArray(new Position[0]);
  }

  private void updateStatus(Position position) throws Exception{
    //get all possible movements for new piece position
    Movement newPossibleMovements[] = this.getAllPossibleMovements(position);
    for (Movement movement: newPossibleMovements) {
      Piece piece = this.getPieceAt(movement.getPosition2());
      if (piece != null && piece.getClass() == King.class) {
        //if piece can hit king it's a check
        this.status = CHECK;
        if (this.isKingOnMate(movement.getPosition2())) {
          this.status = MATE;
        }
      }
    }
  }

  private Piece getPieceAt(Position position) {
    return this.board.getRows()[position.getY()].getSquares()[position.getX()].getPiece();
  }

  private boolean isKingOnMate(Position kingPosition) throws Exception {
    Movement possibleKingMovements[] = this.getAllPossibleMovements(kingPosition);
    Piece piece = this.getPieceAt(kingPosition);
    if (piece.getClass() != King.class) {
      throw new Exception("Piece is not King");
    }
    boolean mate = true;
    for (Movement movement: possibleKingMovements) {
      Position positions[];
      if (piece.getColor() == Piece.WHITE) {
        positions = this.getAllPiecesPositions(Piece.BLACK);
      }else {
        positions = this.getAllPiecesPositions(Piece.WHITE);
      }
      for (Position position: positions) {
        boolean pieceCanHitKing = false;
        Movement possibleMovements[] = this.getAllPossibleMovements(position);
        for (Movement possibleMovement: possibleMovements) {
          Piece targetPiece = this.getPieceAt(possibleMovement.getPosition2());
          if (targetPiece.getClass() == King.class) {
            pieceCanHitKing = true;
          }
        }
        if (!pieceCanHitKing) {
          mate = false;
        }
      }
    }
    return mate;
  }

}
