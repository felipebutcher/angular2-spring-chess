package com.dynadrop.chess.model;

import com.dynadrop.chess.model.Player;
import com.dynadrop.chess.model.Board;
import com.dynadrop.chess.model.Player;

public class Game {
  private Board board;
  private Player player1;
  private Player player2;
  private String uuid;

  public Game(Player player1, String uuid) {
    this.board = new Board();
    this.player1 = player1;
    this.uuid = uuid;
  }

  public Board getBoard() {
    return this.board;
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

}
