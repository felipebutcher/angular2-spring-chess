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
  private boolean isPromotion;
  private Movement lastMovement;
  private Piece lastPieceAtPosition1;
  private Piece lastPieceAtPosition2;
  private ArrayList<String> webSocketSessionIds;

  public static final int STARTED = 0;
  public static final int CHECK = 1;
  public static final int CHECKMATE = 2;


  //TODO if game not found redirect to splash
  public Game(String uuid) {
    this.board = new Board();
    this.uuid = uuid;
    this.status = STARTED;
    this.turnColor = Piece.WHITE;
    this.isPromotion = false;
    this.webSocketSessionIds = new ArrayList<String>();
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

  public void setPlayer1(Player player) {
    this.player1 = player;
  }

  public void setPlayer2(Player player) {
    this.player2 = player;
  }

  public Player getPlayerByUUID(String uuid) {
    if (this.player1 != null && this.player1.getUUID().equals(uuid)) {
      return player1;
    }else if (this.player2 != null && this.player2.getUUID().equals(uuid)) {
      return player2;
    }else {
      return null;
    }
  }

  public String getUUID() {
    return uuid;
  }

  public void doPromote(String pieceName) {
    Position pawnPosition = this.getPawnToPromotePosition();
    Pawn pawn = (Pawn) this.board.getPieceAt(pawnPosition);
    Piece piece = this.getNewPieceByName(pieceName, pawn.getColor());
    this.isPromotion = false;
    this.switchTurnColor();
    this.board.setPieceAt(pawnPosition, piece);
  }

  public Piece getNewPieceByName(String name, int color) {
    if (name.equals("Queen")) {
      return new Queen(color);
    } else if (name.equals("Knight")) {
      return new Knight(color);
    } else if (name.equals("Rook")) {
      return new Rook(color);
    } else if (name.equals("Bishop")) {
      return new Bishop(color);
    }
    return null;
  }

  public Position getPawnToPromotePosition() {
    Position positions[] = this.getAllPiecesPositions(null);
    for (Position position: positions) {
      Piece piece = this.board.getPieceAt(position);
      if (piece.getClass().equals(Pawn.class) &&
          piece.getColor() == Piece.WHITE &&
          position.getY() == 0) {
        return position;
      } else if (piece.getClass().equals(Pawn.class) &&
          piece.getColor() == Piece.BLACK &&
          position.getY() == 7) {
        return position;
      }
    }
    return null;
  }

  public int getStatus() throws Exception {
    if (this.isOnCheckMate(Piece.WHITE) || this.isOnCheckMate(Piece.BLACK)) {
      this.status = CHECKMATE;
    } else if (this.isOnCheck(Piece.WHITE) || this.isOnCheck(Piece.BLACK)) {
      this.status = CHECK;
    } else {
      this.status = STARTED;
    }
    return this.status;
  }

  public int getTurnColor() {
    return this.turnColor;
  }

  public ArrayList<String> getWebSocketSessionIds() {
    return this.webSocketSessionIds;
  }

  public void addWebSocketSessionId(String webSocketSessionId) {
    boolean exists = false;
    for (String id: this.webSocketSessionIds) {
      if (id.equals(webSocketSessionId)) {
        exists = true;
      }
    }
    if (!exists) {
      this.webSocketSessionIds.add(webSocketSessionId);
    }
  }

  public boolean movePiece(Movement movement) throws Exception{
    Piece piece = this.board.getPieceAt(movement.getPosition1());
    if (piece.getColor() != this.turnColor) {
      return false;
    }
    Movement possibleMovements[] = this.getAllPossibleMovements(movement.getPosition1());
    for(Movement possibleMovement: possibleMovements) {
      int enemyColor = this.getEnemyColor(piece.getColor());
      if (movement.equals(possibleMovement) &&
          !this.isOnCheckAfterMovement(piece.getColor(), movement) &&
          this.status != CHECKMATE) {
        if (this.isCastling(movement)) {
          this.doCastling(movement);
        } else {
          this.board.setPieceAt(movement.getPosition1(), null);
          this.board.setPieceAt(movement.getPosition2(), piece);
        }
        System.out.println(this.board);
        Piece pieceAfterMove = this.board.getPieceAt(movement.getPosition2());
        pieceAfterMove.setMoved(true);
        this.switchTurnColor();
        this.isOnCheck(this.getEnemyColor(piece.getColor()));
        return true;
      }
    }
    return false;
  }

  //TODO filter possible movements

  public boolean isPromotion(Movement movement) {
    Piece piece = this.board.getPieceAt(movement.getPosition2());
    System.out.println("piece.getClass() = " + piece.getClass());
    System.out.println("piece.getColor() = " + piece.getColor());
    System.out.println("movement.getPosition2().getY() = " + movement.getPosition2().getY());
    if (piece.getClass().equals(Pawn.class) &&
        piece.getColor() == Piece.WHITE &&
        movement.getPosition2().getY() == 0) {
      this.switchTurnColor();
      this.isPromotion = true;
      return true;
    } else if (piece.getClass().equals(Pawn.class) &&
        piece.getColor() == Piece.BLACK &&
        movement.getPosition2().getY() == 7) {
      this.switchTurnColor();
      this.isPromotion = true;
      return true;
    }
    this.isPromotion = false;
    return false;
  }

  private boolean isCastling(Movement movement) {
    if (this.board.getPieceAt(movement.getPosition1()).getClass().equals(King.class) &&
       (movement.getPosition1().getX() == movement.getPosition2().getX() - 2 ||
        movement.getPosition1().getX() == movement.getPosition2().getX() + 2)) {
      return true;
    }
    return false;
  }

  private void doCastling(Movement movement) {
    System.out.println("castling done");
    int y = movement.getPosition2().getY();
    if (movement.getPosition1().getX() == movement.getPosition2().getX() + 2) {
      //castling left
      Rook rook = (Rook) this.board.getPieceAt(new Position(0, y));
      this.board.setPieceAt(new Position(0, y), null);
      King king = (King) this.board.getPieceAt(movement.getPosition1());
      this.board.setPieceAt(movement.getPosition1(), null);
      this.board.setPieceAt(new Position(2, y), king);
      this.board.setPieceAt(new Position(3, y), rook);
    }else if (movement.getPosition1().getX() == movement.getPosition2().getX() - 2) {
      //castling right
      Rook rook = (Rook) this.board.getPieceAt(new Position(7, y));
      this.board.setPieceAt(new Position(7, y), null);
      King king = (King) this.board.getPieceAt(movement.getPosition1());
      this.board.setPieceAt(movement.getPosition1(), null);
      this.board.setPieceAt(new Position(5, y), rook);
      this.board.setPieceAt(new Position(6, y), king);
    }
  }

  private void switchTurnColor() {
    if (this.turnColor == Piece.WHITE) {
      this.turnColor = Piece.BLACK;
    } else {
      this.turnColor = Piece.WHITE;
    }
  }

  public void undoMove() {
    this.board.setPieceAt(this.lastMovement.getPosition1(), this.lastPieceAtPosition1);
    this.board.setPieceAt(this.lastMovement.getPosition2(), this.lastPieceAtPosition2);
    this.switchTurnColor();
    this.lastPieceAtPosition1.setMoved(false);
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
      for (Direction direction: directions) {
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
    if (pieceAtDestination == null ||
        piece.getColor() != pieceAtDestination.getColor() ||
        (piece.getClass().equals(King.class) &&
         pieceAtDestination.getClass().equals(Rook.class))) {
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
        if (piece != null && (color == null || piece.getColor() == color)) {
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

  private boolean isOnCheckAfterMovement(int color, Movement movement) throws Exception {
    Piece pieceAtPosition1 = this.board.getPieceAt(movement.getPosition1());
    Piece pieceAtPosition2 = this.board.getPieceAt(movement.getPosition2());
    this.board.setPieceAt(movement.getPosition1(), null);
    this.board.setPieceAt(movement.getPosition2(), pieceAtPosition1);
    boolean isOnCheckAfterMovement = this.isOnCheck(color);
    this.board.setPieceAt(movement.getPosition1(), pieceAtPosition1);
    this.board.setPieceAt(movement.getPosition2(), pieceAtPosition2);
    return isOnCheckAfterMovement;
  }

  private boolean isOnCheckMate(int color) throws Exception {
    Position allPositions[] = this.getAllPiecesPositions(color);
    for (Position position: allPositions) {
      Movement movements[] = this.getAllPossibleMovements(position);
      for (Movement movement: movements) {
        this.saveInfoForUndo(movement);
        boolean moved = this.movePiece(movement);
        boolean isOnCheck = this.isOnCheck(color);
        if (moved) this.undoMove();
        if (!isOnCheck) return false;
      }
    }
    this.status = CHECKMATE;
    return true;
  }

  private boolean isOnCheck(int color) {
    Position kingPosition = this.board.getKingPosition(color);
    int enemyColor = this.getEnemyColor(color);
    Position allEnemyPositions[] = this.getAllPiecesPositions(enemyColor);
    for (Position enemyPosition: allEnemyPositions) {
      if (this.pieceCanHitEnemyKing(enemyPosition)){
        this.status = CHECK;
        return true;
      }
    }
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

  @Override
  protected Object clone() throws CloneNotSupportedException {
      return super.clone();
  }

}
