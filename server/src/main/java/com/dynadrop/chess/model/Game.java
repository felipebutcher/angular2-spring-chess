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
  private int turn;

  public static final int STARTED = 0;
  public static final int CHECK = 1;
  public static final int MATE = 2;


  public Game(Player player1, String uuid) {
    this.board = new Board();
    this.player1 = player1;
    this.uuid = uuid;
    this.status = STARTED;
    this.turn = Piece.WHITE;
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

  public int getTurn() {
    return this.turn;
  }

  public boolean movePiece(Movement movement) throws Exception{
    Piece piece = this.board.getPieceAt(movement.getPosition1());
    if (piece.getColor() != this.turn) {
      System.out.println("Movement is NOT VALID, wrong player turn");
      return false;
    }
    Movement possibleMovements[] = this.getAllPossibleMovements(movement.getPosition1());
    System.out.println("Requested movement: "+movement);
    for(Movement possibleMovement: possibleMovements) {
      System.out.println("Possible movement: "+possibleMovement);
      if (movement.equals(possibleMovement)) {
        System.out.println(this.board);
        System.out.println("Movement is VALID");
        this.board.setPieceAt(movement.getPosition1(), null);
        this.board.setPieceAt(movement.getPosition2(), piece);
        this.updateStatus(movement.getPosition2());
        if (this.turn == Piece.WHITE) {
          this.turn = Piece.BLACK;
        } else {
          this.turn = Piece.WHITE;
        }
        return true;
      }
    }
    System.out.println("Movement is NOT VALID");
    return false;
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

  public Position[] getAllPiecesPositions(int color) {
    ArrayList<Position> positions = new ArrayList<Position>();
    for (int x=0; x<=7; x++) {
      for (int y=0; y<=7; y++) {
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
    System.out.println("Updating status...");
    for (Movement movement: newPossibleMovements) {
      Piece piece = this.board.getPieceAt(movement.getPosition2());
      System.out.println("Possible movement:" + movement);
      if (piece != null && piece.getClass().equals(King.class)) {
        //if piece can hit king it's a check
        this.status = CHECK;
        System.out.println("CHECK DETECTED");
        if (this.isKingOnMate(movement.getPosition2())) {
          this.status = MATE;
          System.out.println("MATE DETECTED");
        }
      }
    }
    System.out.println("Game status: "+this.status);
  }

  private boolean isKingOnMate(Position kingPosition) throws Exception {
    Movement possibleKingMovements[] = this.getAllPossibleMovements(kingPosition);
    Piece piece = this.board.getPieceAt(kingPosition);
    boolean mate = false;
    for (Movement movement: possibleKingMovements) {
      Position enemyPositions[];
      if (piece.getColor() == Piece.WHITE) {
        enemyPositions = this.getAllPiecesPositions(Piece.BLACK);
      }else {
        enemyPositions = this.getAllPiecesPositions(Piece.WHITE);
      }
      for (Position enemyPosition: enemyPositions) {
        if (this.pieceCanHitKing(enemyPosition)) {
          mate = true;
        }
      }
    }
    return mate;
  }

  private boolean pieceCanHitKing (Position position) {
    boolean pieceCanHitKing = false;
    Movement possibleMovements[] = this.getAllPossibleMovements(position);
    for (Movement possibleMovement: possibleMovements) {
      Piece targetPiece = this.board.getPieceAt(possibleMovement.getPosition2());
      if (targetPiece != null && targetPiece.getClass().equals(King.class)) {
        pieceCanHitKing = true;
      }
    }
    return true;
  }

}
