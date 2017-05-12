import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';

import { RouterModule } from '@angular/router';
import { routing } from './app.routing';

import { AppComponent } from './app.component';
import { BoardComponent } from './board/board.component';
import { SplashComponent } from './splash/splash.component';

import { ModalModule } from 'angular2-modal';
import { BootstrapModalModule } from 'angular2-modal/plugins/bootstrap';
import { PromotionModal } from './promotion-modal';


@NgModule({
  declarations: [
    AppComponent,
    BoardComponent,
    SplashComponent,
    PromotionModal
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    routing,
    ModalModule.forRoot(),
    BootstrapModalModule
  ],
  providers: [],
  bootstrap: [AppComponent],
  entryComponents: [PromotionModal]
})
export class AppModule { }
