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
    path: 'game/:gameUUID',
    component: BoardComponent
  }
];

export const routing: ModuleWithProviders = RouterModule.forRoot(appRoutes, { useHash: true });
