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
    this.ws = new $WebSocket();
    this.sendNewMatchRequest();
    this.waitNewMatchRespose();
  }

  sendNewMatchRequest() {
    let uuid = UUID.UUID();
    localStorage.setItem('gameUUID', uuid);
    localStorage.setItem('myPlayerNumber', '0');
    let message:Message = {
      action: 'newGame',
      gameUUID: uuid
    }
    this.ws.send(message);
  }

  waitNewMatchRespose() {
    this.ws.getDataStream().subscribe(
      res => {
        let game = JSON.parse(res.data);
        if(game.uuid) {
          this.router.navigate(['/game/'+game.uuid]);
        }
      },
      function(e) { console.log('Error: ' + e.message); },
      function() { console.log('Completed'); }
    );
  }

}


interface Message {
  action: string;
  gameUUID: string;
}
