package com.dynadrop.chess.websocket.bean;

import com.dynadrop.chess.model.Board;

public class Movement {
  private int x1;
  private int y1;
  private int x2;
  private int y2;

  public final int NO_MOVE = 0;
  public final int HORIZONTAL = 0;
  public final int VERTICAL = 0;
  public final int DIAGONAL = 0;
  public final int KNIGHT = 0;
  public final int INVALID = 0;


  public boolean isDestinationWithinBoard() {
    boolean is = this.x2>=0 && this.x2<8 &&
            this.y2>=0 && this.y2<8;
    System.out.println("isDestinationWithinBoard:" + is);
    return is;
  }

  public double getDistance() {
    double distance = Math.sqrt(Math.pow(this.x2 - this.x1, 2) +
                                Math.pow(this.y2 - this.y1, 2));
    System.out.println("movement distance: "+distance);
    return distance;
  }

  public int getMovementType() {
    if (this.y1 == this.y2 && this.x1 == this.x2) {
      return NO_MOVE;
    } else if (this.y1 == this.y2) {
      return HORIZONTAL;
    } else if (this.x1 == this.x2) {
      return VERTICAL;
    } else if (getGradient() == 1) {
      return DIAGONAL;
    } else if (this.isKnight()) {
      return KNIGHT;
    } else {
      return INVALID;
    }
  }

  private boolean isKnight() {
    return this.getGradient() == 1.5;//TODO
  }

  private double getGradient() {//TODO private
    System.out.println("gradient: "+this.getGradient());
    return (this.y2-this.y1)/(this.x2-this.x1);
  }

  public boolean hasNoPiecesOnTheWay(Board board) {
    //TODO implement
    // if (this.y1 == this.y2) {//horizontal move
    //   if (this.x2>this.x1) {
    //     for(int i=x1+1; i++; i<this.x2) {
    //       if (board.getRows()[this.y1].getSquares()[i].getPiece() != null) {
    //         return false;
    //       }
    //     }
    //   }else {
    //     for(int i=x2; i--; i>this.x1) {
    //       if (board.getRows()[this.y1].getSquares()[i].getPiece() != null) {
    //         return false;
    //       }
    //     }
    //   }
    // }else if (this.x1 == this.x2) {//vertical move
    //   if (this.y2>this.y1) {
    //     for(int i=y1+1; i++; i<this.y2) {
    //       if (board.getRows()[i].getSquares()[this.x1].getPiece() != null) {
    //         return false;
    //       }
    //     }
    //   }else {
    //     for(int i=y2; i--; i>this.y1) {
    //       if (board.getRows()[i].getSquares()[this.x1].getPiece() != null) {
    //         return false;
    //       }
    //     }
    //   }
    // }else {//diagonal move
    //
    // }
    return true;
  }

  public int getX1() {
    return this.x1;
  }

  public void setX1(int value) {
    this.x1 = value;
  }

  public int getY1() {
    return this.y1;
  }

  public void setY1(int value) {
    this.y1 = value;
  }

  public int getX2() {
    return this.x2;
  }

  public void setX2(int value) {
    this.x2 = value;
  }

  public int getY2() {
    return this.y2;
  }

  public void setY2(int value) {
    this.y2 = value;
  }

}
