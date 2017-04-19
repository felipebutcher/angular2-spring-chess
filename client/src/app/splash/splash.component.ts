import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-splash',
  templateUrl: './splash.component.html',
  styleUrls: ['./splash.component.css']
})
export class SplashComponent implements OnInit {

  constructor(private router: Router) { }

  ngOnInit() {
  }

  play() {
    console.log("play");
    //this.gameService.create().subscribe(response => {
      //if (response != false) {
        this.router.navigate(['/game/'+1]);//TODO response.gameId
      //}
    //});
  }

}
