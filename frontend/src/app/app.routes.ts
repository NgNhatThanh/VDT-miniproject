import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { HOME_ROUTES } from './pages/home/home.routes';
import { MeetingComponent } from './pages/meeting/meeting.component';

export const routes: Routes = [
    {
        path: '',
        component: HomeComponent,
        children: [
            ...HOME_ROUTES
        ]
    },
    {
        path: 'cuoc-hop/:meetingId',
        component: MeetingComponent
    },
    {
        path: '**',
        redirectTo: ''
    }
];
