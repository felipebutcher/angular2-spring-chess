import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { $WebSocket } from '../services/websocket.service';

@Component({
  selector: 'app-board',
  templateUrl: './board.component.html',
  styleUrls: ['./board.component.css']
})
export class BoardComponent implements OnInit {
  private ws: $WebSocket;
  public  boardSize: number;
  public  squareSize: number;
  private fontSize: number;
  private gameUUID: string;
  private sub: any;
  private movement: Movement;
  private myPlayerNumber: number;
  private lastStatus: number;
  game: any;


  constructor(private route: ActivatedRoute, private router: Router) {
    this.movement = { position1: {x: null, y: null}, position2: {x: null, y: null} };
  }

  ngOnInit() {
    if (!localStorage.getItem("myPlayerNumber")) {
      localStorage.setItem("myPlayerNumber", "1");
    }
    this.myPlayerNumber = +localStorage.getItem("myPlayerNumber");
    this.game = {};
    this.game.board = {};
    this.game.board.rows = [];
    this.sub = this.route.params.subscribe(params => {
      this.game.board.rows = [];
      this.gameUUID = params["gameUUID"];
      this.ws = new $WebSocket("ws://192.168.1.114:8088/game");
      this.requestUpdate();
    });
  }

  click(x, y) {
    this.resetColors();
    if (this.game.status == 2) {
      return;
    }
    if(this.game.board.rows[y].squares[x].piece &&
       this.game.board.rows[y].squares[x].piece.color == this.myPlayerNumber) {
      this.startMovement(x, y);
    }else if (this.movement.position1.x != null) {
      this.completeMovement(x, y);
    }
  }

  startMovement(x: number, y: number) {
    if (this.game.turnColor != this.myPlayerNumber) {
      return;
    }
    this.game.board.rows[y].squares[x].color = "red";
    let movement = { position1: {x: x, y: y}, position2: {x: null, y: null} };
    if (this.myPlayerNumber == 1) {
      movement = { position1: {x: 7-x, y: 7-y}, position2: {x: null, y: null} }
    }
    let message:Message = {
      action: 'requestPossibleMovements',
      movement: movement,
      gameUUID: this.gameUUID
    }
    this.ws.send(message);
    this.movement = movement;
  }

  completeMovement(x: number, y: number) {
    this.movement.position2.x = x;
    this.movement.position2.y = y;
    if (this.myPlayerNumber == 1) {
      this.movement.position2.x = 7-x;
      this.movement.position2.y = 7-y;
    }
    let message:Message = {
      action: 'move',
      movement: this.movement,
      gameUUID: this.gameUUID
    }
    this.ws.send(message);
    this.movement = { position1: {x: null, y: null}, position2: {x: null, y: null} };
  }

  requestUpdate() {
    let movement:Movement = { position1: {x: 0, y: 0}, position2: {x: 0, y: 0} }
    let message:Message = {
      action: 'requestUpdate',
      movement: movement,
      gameUUID: this.gameUUID
    }
    this.ws.getDataStream().subscribe(
      res => {
        if (JSON.parse(res.data).type == "possibleMovements") {
          let availableMovements = JSON.parse(res.data).possibleMovements;
          this.processAvailableMovements(availableMovements);
        }else {
          let game = JSON.parse(res.data);
          this.updateBoard(game);
        }
      },
      function(e) { console.log('Error: ' + e.message); },
      function() { console.log('Completed'); }
    );
    this.ws.send(message);
  }

  processAvailableMovements(availableMovements: any) {
    if (this.game.turnColor != this.myPlayerNumber) {
      return;
    }
    for (let movement of availableMovements) {
      if (this.myPlayerNumber == 1) {
        movement.position2.x = 7-movement.position2.x;
        movement.position2.y = 7-movement.position2.y;
      }
      this.game.board.rows[movement.position2.y].squares[movement.position2.x].color = "green";
    }
  }

  updateBoard(game: any) {
    if (game.status == 1 && game.status != this.lastStatus) {
      alert("CHECK");
    }
    if (game.status == 2 && game.status != this.lastStatus) {
      alert("CHECKMATE");
    }
    this.lastStatus = game.status;
    if (this.myPlayerNumber == 1) {
      game.board = this.invertBoard(game.board);
    }
    this.game = game;
    this.playBeep();
  }

  playBeep() {
    let audio = new Audio();
    audio.src = "http://cam.dynadrop.com/chess/chessmove.mp3";
    audio.load();
    audio.play();
  }

  ngAfterViewInit() {
    setTimeout(() => {
      this.resize();
    }, 1);
  }

  resize() {
    this.boardSize = Math.min(window.innerHeight, window.innerWidth) - 20;
    this.squareSize = this.boardSize/8;
    this.fontSize = Math.floor(100*this.squareSize/16/1.8)/100;

  }

  invertBoard(board) {
    board.rows = board.rows.slice().reverse();
    for (let row of board.rows) {
      row.squares = row.squares.slice().reverse();
    }
    return board;
  }

  resetColors() {
    for (let x=0; x<=7; x++) {
      for (let y=0; y<=7; y++) {
        this.game.board.rows[y].squares[x].color = null;
      }
    }
  }

}


interface Message {
  action: string;
  movement: Movement;
  gameUUID: string;
}

interface Movement {
  position1: Position;
  position2: Position;
}

interface Position {
  x: number;
  y: number;
}
