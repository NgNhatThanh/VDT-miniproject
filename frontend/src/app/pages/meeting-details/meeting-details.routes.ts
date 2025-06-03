import { Routes } from '@angular/router';

export const MEETING_DETAILS_ROUTES: Routes = [
    {
        path: '',
        redirectTo: 'thong-tin',
        pathMatch: 'full'
    },
    {
        path: 'thong-tin',
        loadComponent: () => import('./meeting-info/meeting-info.component').then(m => m.MeetingInfoComponent)
    },
    // {
    //     path: 'thanh-phan-tham-du',
    //     loadComponent: () => import('../personal-meetings/personal-meetings.component').then(m => m.PersonalMeetingsComponent)
    // }
]; 