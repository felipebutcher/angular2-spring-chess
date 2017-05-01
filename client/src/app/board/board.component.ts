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
  game: any;


  constructor(private route: ActivatedRoute, private router: Router) {
    this.movement = { position1: {x: null, y: null}, position2: {x: null, y: null} };
  }

  ngOnInit() {
    this.sub = this.route.params.subscribe(params => {
      this.game = JSON.parse(localStorage.getItem("game"));
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
              console.log(movement.position2.x + "," + movement.position2.y);
              this.game.board.rows[movement.position2.y].squares[movement.position2.x].color = "green";
            }
          }else {
            let game = JSON.parse(res.data);
            console.log('received game update: ' + game.uuid);
            console.log(game);
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
    console.log("clicked "+x+","+y);
    if(this.game.board.rows[y].squares[x].piece) {
      this.game.board.rows[y].squares[x].color = "red";
      console.log("start movement");
      let message:Message = {
        action: 'requestPossibleMovements',
        movement: { position1: {x: x, y: y}, position2: {x: null, y: null} },
        gameUUID: this.gameUUID
      }
      this.ws.send(message);
      this.movement.position1.x = x;
      this.movement.position1.y = y;
    }else if (this.movement.position1.x != null) {
      console.log("make movement now");
      this.movement.position2.x = x;
      this.movement.position2.y = y;
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
