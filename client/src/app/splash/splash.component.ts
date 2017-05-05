import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { $WebSocket } from '../services/websocket.service';
import { UUID } from 'angular2-uuid';

@Component({
  selector: 'app-splash',
  templateUrl: './splash.component.html',
  styleUrls: ['./splash.component.css']
})
export class SplashComponent implements OnInit {
  ws: $WebSocket;

  constructor(private router:Router) { }

  ngOnInit() {
    localStorage.clear();
  }

  play() {
    console.log("Connecting to websocket...");
    this.ws = new $WebSocket("ws://104.131.146.200:8088/game");
    console.log("Creating new match...");
    this.sendNewMatchRequest();
    this.waitNewMatchRespose();
  }

  sendNewMatchRequest() {
    let movement:Movement = { x1: 0, y1: 0, x2: 0, y2: 0 }
    let uuid = UUID.UUID();
    console.log("stored gameuuid: " + uuid);
    localStorage.setItem('gameUUID', uuid);
    localStorage.setItem("myPlayerNumber", "0");
    let message:Message = {
      action: 'newGame',
      movement: movement,
      gameUUID: uuid
    }
    this.ws.send(message);
  }

  waitNewMatchRespose() {
    this.ws.getDataStream().subscribe(
      res => {
        let game = JSON.parse(res.data);
        if(game.uuid) {
          console.log('Match created with uuid: ' + game.uuid);
          this.router.navigate(['/game/'+game.uuid]);
          localStorage.setItem("game", JSON.stringify(game));
        }
      },
      function(e) { console.log('Error: ' + e.message); },
      function() { console.log('Completed'); }
    );
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
