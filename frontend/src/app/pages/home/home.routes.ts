import { Routes } from '@angular/router';

export const HOME_ROUTES: Routes = [
    {
        path: '',
        redirectTo: 'lich-hop-ca-nhan',
        pathMatch: 'full'
    },
    {
        path: 'lich-hop-ca-nhan',
        loadComponent: () => import('../meeting/meeting.component').then(m => m.MeetingComponent)
    },
    {
        path: 'lich-hop-don-vi',
        loadComponent: () => import('../meeting/meeting.component').then(m => m.MeetingComponent)
    },
    {
        path: 'lich-hop-yeu-cau',
        loadComponent: () => import('../meeting/meeting.component').then(m => m.MeetingComponent)
    },
    {
        path: 'bieu-quyet-ngoai-cuoc-hop',
        loadComponent: () => import('../meeting/meeting.component').then(m => m.MeetingComponent)
    }
]; 