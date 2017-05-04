package com.dynadrop.chess.websocket.bean;


public class Movement {
  private Position position1;
  private Position position2;

  public final int NO_MOVE = 0;
  public final int HORIZONTAL = 0;
  public final int VERTICAL = 0;
  public final int DIAGONAL = 0;
  public final int KNIGHT = 0;
  public final int INVALID = 0;

  public Movement(Position position1, Position position2) {
    this.position1 = position1;
    this.position2 = position2;
  }

  public Position getPosition1() {
    return this.position1;
  }

  public Position getPosition2() {
    return this.position2;
  }

  @Override
  public boolean equals(Object object) {
    Movement movement = (Movement) object;
    return this.position1.getX() == movement.getPosition1().getX() &&
           this.position1.getY() == movement.getPosition1().getY() &&
           this.position2.getX() == movement.getPosition2().getX() &&
           this.position2.getY() == movement.getPosition2().getY();
  }

  @Override
  public String toString() {
    return "Movement: "+this.position1+" => "+this.position2;
  }

}
