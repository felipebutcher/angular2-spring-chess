package com.dynadrop.chess.websocket.bean;


public class Direction {
  private int x;
  private int y;
  private int limit;

  public Direction(int x, int y, int limit) {
    this.x = x;
    this.y = y;
    this.limit = limit;
  }

  public int getX() {
    return this.x;
  }

  public int getY() {
    return this.y;
  }

  public int getLimit() {
    return this.limit;
  }

}
