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
  private int turnColor;
  private Movement lastMovement;
  private Piece lastPieceAtPosition1;
  private Piece lastPieceAtPosition2;

  public static final int STARTED = 0;
  public static final int CHECK = 1;
  public static final int CHECKMATE = 2;


  public Game(Player player1, String uuid) {
    this.board = new Board();
    this.player1 = player1;
    this.uuid = uuid;
    this.status = STARTED;
    this.turnColor = Piece.WHITE;
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

  public int getTurnColor() {
    return this.turnColor;
  }

  public boolean movePiece(Movement movement) throws Exception{
    Piece piece = this.board.getPieceAt(movement.getPosition1());
    if (piece.getColor() != this.turnColor) {
      System.out.println("Movement is NOT VALID, wrong player turn");
      return false;
    }
    Movement possibleMovements[] = this.getAllPossibleMovements(movement.getPosition1());
    System.out.println("Requested movement: "+movement);
    for(Movement possibleMovement: possibleMovements) {
      System.out.println("Possible movement: "+possibleMovement);
      int enemyColor = this.getEnemyColor(piece.getColor());
      if (movement.equals(possibleMovement) &&
          !this.isOnCheck(piece.getColor()) &&
          this.status != CHECKMATE) {
        this.saveInfoForUndo(movement);
        this.board.setPieceAt(movement.getPosition1(), null);
        this.board.setPieceAt(movement.getPosition2(), piece);
        System.out.println(this.board);
        System.out.println("Movement is VALID");
        if (this.turnColor == Piece.WHITE) {
          this.turnColor = Piece.BLACK;
        } else {
          this.turnColor = Piece.WHITE;
        }
        this.isOnCheck(this.turnColor);
        /*if (this.isOnCheck(this.turnColor)) {
          this.status = CHECK;
          if (this.isOnCheckMate(this.turnColor)) {
            this.status = CHECKMATE;
          }
        }*/
        return true;
      }
    }
    System.out.println("Movement is NOT VALID");
    return false;
  }

  public void undoMove() {
    this.board.setPieceAt(lastMovement.getPosition1(), this.lastPieceAtPosition1);
    this.board.setPieceAt(lastMovement.getPosition2(), this.lastPieceAtPosition2);
  }

  private void saveInfoForUndo(Movement movement) {
    this.lastMovement = movement;
    this.lastPieceAtPosition1 = this.board.getPieceAt(movement.getPosition1());
    this.lastPieceAtPosition2 = this.board.getPieceAt(movement.getPosition2());
  }

  public Movement[] getAllPossibleMovements(Position position) {
    ArrayList<Movement> movements = new ArrayList<Movement>();
    Piece piece = this.board.getPieceAt(position);
    if (piece != null) {
      Direction directions[] = piece.getDirections(this.board, position);
      //System.out.println("Directions for "+piece.getClass());
      for (Direction direction: directions) {
        //System.out.println("Direction: "+direction.getX()+","+direction.getY()+" limit:"+direction.getLimit());
        Position positionFrom = position;
        Position positionTo = new Position(positionFrom.getX()+direction.getX(), positionFrom.getY()+direction.getY());
        int i = 0;
        int limit = direction.getLimit();
        while (this.canMoveTo(piece, positionTo) && i<limit) {
          Piece pieceAtDestination = this.board.getPieceAt(positionTo);
          if (pieceAtDestination != null && piece.getColor()!=pieceAtDestination.getColor()) {
            limit = i;
          }
          movements.add(new Movement(position, positionTo));
          positionFrom = positionTo;
          positionTo = new Position(positionFrom.getX()+direction.getX(), positionFrom.getY()+direction.getY());
          i++;
        }
      }
    }
    return movements.toArray(new Movement[0]);
  }

  private boolean canMoveTo(Piece piece, Position position) {
    if (!position.isWithinBoard()) {
      return false;
    }
    Piece pieceAtDestination = this.board.getPieceAt(position);
    if (pieceAtDestination == null || piece.getColor()!=pieceAtDestination.getColor()) {
      return true;
    } else {
      return false;
    }
  }

  public Position[] getAllPiecesPositions(Integer color) {
    ArrayList<Position> positions = new ArrayList<Position>();
    for (int x=0; x<=7; x++) {
      for (int y=0; y<=7; y++) {
        Piece piece = this.board.getPieceAt(new Position(x, y));
        if (piece != null && (piece.getColor() == color || color == null)) {
          positions.add(new Position(x, y));
        }
      }
    }
    return positions.toArray(new Position[0]);
  }

  private int getEnemyColor(int color) {
    if (color == Piece.WHITE) return Piece.BLACK;
    else return Piece.WHITE;
  }

  private boolean isOnCheckMate(int color) throws Exception {
    System.out.println("CHECKING IS ON CHECKMATE");
    Position allPositions[] = this.getAllPiecesPositions(color);
    for (Position position: allPositions) {
      Movement movements[] = this.getAllPossibleMovements(position);
      for (Movement movement: movements) {
        this.movePiece(movement);
        if (!this.isOnCheck(color)) {
          System.out.println("NOT ON CHECKMATE");
          return false;
        }
        this.undoMove();
      }
    }
    this.status = CHECKMATE;
    System.out.println("CHECKMATE DETECTED");
    return true;
  }

  protected boolean isOnCheck(int color) {
    System.out.println("CHECKING IS ON CHECK");
    Position kingPosition = this.board.getKingPosition(color);
    int enemyColor = this.getEnemyColor(color);
    Position allEnemyPositions[] = this.getAllPiecesPositions(enemyColor);
    for (Position enemyPosition: allEnemyPositions) {
      if (pieceCanHitEnemyKing(enemyPosition)){
        this.status = CHECK;
        System.out.println("CHECK DETECTED");
        return true;
      }
    }
    System.out.println("NOT ON CHECK");
    return false;
  }

  private boolean pieceCanHitEnemyKing (Position position) {
    Piece piece = this.board.getPieceAt(position);
    boolean pieceCanHitKing = false;
    Movement possibleMovements[] = this.getAllPossibleMovements(position);
    for (Movement possibleMovement: possibleMovements) {
      Piece targetPiece = this.board.getPieceAt(possibleMovement.getPosition2());
      if (targetPiece != null && targetPiece.getClass().equals(King.class) &&
          targetPiece.getColor() != piece.getColor()) {
        pieceCanHitKing = true;
      }
    }
    return pieceCanHitKing;
  }

}
