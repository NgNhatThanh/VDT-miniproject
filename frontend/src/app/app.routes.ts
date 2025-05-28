import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { HOME_ROUTES } from './pages/home/home.routes';

export const routes: Routes = [
    {
        path: '',
        component: HomeComponent,
        children: [
            ...HOME_ROUTES
        ]
    },
    {
        path: '**',
        redirectTo: ''
    }
];
