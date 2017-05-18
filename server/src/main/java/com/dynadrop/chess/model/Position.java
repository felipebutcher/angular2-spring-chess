package com.dynadrop.chess.model;

public class Position {
  private int x;
  private int y;

  public Position(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return this.x;
  }

  public int getY() {
    return this.y;
  }

  public boolean isWithinBoard() {
    return this.x>=0 && this.x<=7 && this.y>=0 && this.y<=7;
  }

  @Override
  public String toString() {
    return "("+this.x+","+this.y+")";
  }

}
