import { Routes } from '@angular/router';

export const HOME_ROUTES: Routes = [
    {
        path: '',
        redirectTo: 'lich-hop-ca-nhan',
        pathMatch: 'full'
    },
    {
        path: 'them-cuoc-hop',
        loadComponent: () => import('../add-meeting/add-meeting.component').then(m => m.AddMeetingComponent)
    },
    {
        path: 'lich-hop-ca-nhan',
        loadComponent: () => import('../personal-meetings/personal-meetings.component').then(m => m.PersonalMeetingsComponent)
    },
    {
        path: 'chi-tiet-cuoc-hop/:meetingId',
        loadComponent: () => import('../meeting-details/meeting-details.component').then(m => m.MeetingDetailsComponent)
    }
]; 