import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { $WebSocket } from '../services/websocket.service';
import { UUID } from 'angular2-uuid';

@Component({
  selector: 'app-splash',
  templateUrl: './splash.component.html',
  styleUrls: ['./splash.component.css']
})
export class SplashComponent {
  ws: $WebSocket;

  constructor(private router:Router) { }

  play() {
    console.log("Connecting to websocket...");
    this.ws = new $WebSocket("ws://192.168.1.114:8088/game");
    console.log("Creating new match...");
    this.sendNewMatchRequest();
    this.waitNewMatchRespose();
  }

  sendNewMatchRequest() {
    let position:Position = {
      x: 0,
      y: 0
    }
    let uuid = UUID.UUID();
    console.log("stored gameuuid: " + uuid);
    localStorage.setItem('gameUUID', uuid);
    let message:Message = {
      action: 'newGame',
      positionFrom: position,
      positionTo: position,
      gameUUID: uuid
    }
    this.ws.send(message);
  }

  waitNewMatchRespose() {
    this.ws.getDataStream().subscribe(
      res => {
        let game = JSON.parse(res.data);
        console.log('Match created with uuid: ' + game.uuid);
        this.router.navigate(['/game/'+game.uuid]);
      },
      function(e) { console.log('Error: ' + e.message); },
      function() { console.log('Completed'); }
    );
  }

}


interface Message {
  action: string;
  positionFrom: Position;
  positionTo: Position;
  gameUUID: string;
}

interface Position {
  x: number;
  y: number;
}
