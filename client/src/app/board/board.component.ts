import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-board',
  templateUrl: './board.component.html',
  styleUrls: ['./board.component.css']
})
export class BoardComponent implements OnInit {
  boardSize: number;
  fontSize: number;
  gameUUID: string;
  private sub: any;

  constructor(private route: ActivatedRoute, private router: Router) {

  }

  ngOnInit() {
    this.sub = this.route.params.subscribe(params => {
      this.gameUUID = params["gameUUID"];
      console.log("gameUUID: "+this.gameUUID);
    });
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
