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
  private boardSize: number;
  private fontSize: number;
  private gameUUID: string;
  private sub: any;
  private movement: Movement;
  private myPlayerNumber: number;
  game: any;


  constructor(private route: ActivatedRoute, private router: Router) {
    this.movement = { position1: {x: null, y: null}, position2: {x: null, y: null} };
  }

  ngOnInit() {
    if (!localStorage.getItem("myPlayerNumber")) {
      localStorage.setItem("myPlayerNumber", "1");
    }
    this.myPlayerNumber = +localStorage.getItem("myPlayerNumber");
    console.log("myPlayerNumber: "+this.myPlayerNumber);
    this.game = {};
    this.game.board = {};
    this.game.board.rows = [];
    this.sub = this.route.params.subscribe(params => {
      //this.game = JSON.parse(localStorage.getItem("game"));
      this.game.board.rows = [];
      this.gameUUID = params["gameUUID"];
      console.log("gameUUID: "+this.gameUUID);
      this.ws = new $WebSocket("ws://192.168.1.114:8088/game");
      let movement:Movement = { position1: {x: 0, y: 0}, position2: {x: 0, y: 0} }
      let message:Message = {
        action: 'requestUpdate',
        movement: movement,
        gameUUID: this.gameUUID
      }
      this.ws.getDataStream().subscribe(
        res => {
          console.log(JSON.parse(res.data).type);
          if (JSON.parse(res.data).type == "possibleMovements") {
            let availableMovements = JSON.parse(res.data).possibleMovements;
            console.log("received possible movements");
            for (let movement of availableMovements) {
              if (this.myPlayerNumber == 1) {
                movement.position2.x = 7-movement.position2.x;
                movement.position2.y = 7-movement.position2.y;
              }
              console.log(movement.position2.x + "," + movement.position2.y);
              this.game.board.rows[movement.position2.y].squares[movement.position2.x].color = "green";
            }
          }else {
            let game = JSON.parse(res.data);
            console.log('received game update: ' + game.uuid);
            console.log(game);
            if (this.myPlayerNumber == 1) {
              game.board = this.invertBoard(game.board);
            }
            this.game = game;
          }
        },
        function(e) { console.log('Error: ' + e.message); },
        function() { console.log('Completed'); }
      );
      this.ws.send(message);
    });
  }

  click(x, y) {
    console.log("this.myPlayerNumber="+this.myPlayerNumber);
    console.log(this.myPlayerNumber == 1);
    console.log("clicked "+x+","+y);
    if(this.game.board.rows[y].squares[x].piece &&
       this.game.board.rows[y].squares[x].piece.color == this.myPlayerNumber) {
      if (this.game.turn != this.myPlayerNumber) {
        return;
      }
      this.game.board.rows[y].squares[x].color = "red";
      console.log("start movement");
      let movement = { position1: {x: x, y: y}, position2: {x: null, y: null} };
      if (this.myPlayerNumber == 1) {
        console.log("entrou nessa porra");
        movement = { position1: {x: 7-x, y: 7-y}, position2: {x: null, y: null} }
      }
      console.log("MOVEMENT: ");
      console.log(this.movement);
      let message:Message = {
        action: 'requestPossibleMovements',
        movement: movement,
        gameUUID: this.gameUUID
      }
      this.ws.send(message);
      this.movement = movement;
    }else if (this.movement.position1.x != null) {
      console.log("make movement now");
      console.log(this.myPlayerNumber);
      console.log(this.myPlayerNumber == 0);
      this.movement.position2.x = x;
      this.movement.position2.y = y;
      if (this.myPlayerNumber == 1) {
        this.movement.position2.x = 7-x;
        this.movement.position2.y = 7-y;
      }
      console.log("MOVEMENT: ");
      console.log(this.movement);
      let message:Message = {
        action: 'move',
        movement: this.movement,
        gameUUID: this.gameUUID
      }
      this.ws.send(message);
      //reset movement
      this.movement = { position1: {x: null, y: null}, position2: {x: null, y: null} };
    }

  }

  ngAfterViewInit() {
    setTimeout(() => {
      this.resize();
    }, 1);
  }

  resize() {
    this.boardSize = Math.min(window.innerHeight, window.innerWidth) - 20;
    let squareSize = this.boardSize/8;
    this.fontSize = Math.floor(100*squareSize/16/1.8)/100;
  }

  invertBoard(board) {
    board.rows = board.rows.slice().reverse();
    for (let row of board.rows) {
      row.squares = row.squares.slice().reverse();
    }
    return board;
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
