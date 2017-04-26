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
    this.movement = { x1: null, y1: null, x2: null, y2: null };
  }

  ngOnInit() {
    this.sub = this.route.params.subscribe(params => {
      this.game = JSON.parse(localStorage.getItem("game"));
      this.gameUUID = params["gameUUID"];
      console.log("gameUUID: "+this.gameUUID);
      this.ws = new $WebSocket("ws://192.168.1.114:8088/game");
      let movement:Movement = { x1: 0, y1: 0, x2: 0, y2: 0 }
      let message:Message = {
        action: 'requestUpdate',
        movement: movement,
        gameUUID: this.gameUUID
      }
      this.ws.getDataStream().subscribe(
        res => {
          let game = JSON.parse(res.data);
          console.log('received game update: ' + game.uuid);
          console.log(game);
          this.game = game;
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
      this.movement.x1 = x;
      this.movement.y1 = y;
    }else if (this.movement.x1 != null) {
      console.log("make movement now");
      this.movement.x2 = x;
      this.movement.y2 = y;
      let message:Message = {
        action: 'move',
        movement: this.movement,
        gameUUID: this.gameUUID
      }
      this.ws.send(message);
      //reset movement
      this.movement = { x1: null, y1: null, x2: null, y2: null };
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
  x1: number;
  y1: number;
  x2: number;
  y2: number;
}
