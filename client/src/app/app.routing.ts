import { ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { BoardComponent } from './board/board.component';
import { SplashComponent } from './splash/splash.component';

const appRoutes : Routes = [
  {
    path: '',
    component: SplashComponent
  },
  {
    path: 'game/:gameId',
    component: BoardComponent
  }/*,
  {
    path: 'reservation/:standId',
    component: ReservationComponent
  }*/
];

export const routing: ModuleWithProviders = RouterModule.forRoot(appRoutes);
