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
    {
        path: 'thanh-phan-tham-du',
        loadComponent: () => import('./participants-list/participants-list.component').then(m => m.ParticipantsListComponent)
    },
    {
        path: 'tai-lieu',
        loadComponent: () => import('./documents-list/documents-list.component').then(m => m.DocumentsListComponent)
    },
    {
        path: 'bieu-quyet',
        loadComponent: () => import('./voting-list/voting-list.component').then(m => m.VotingListComponent)
    },
    {
        path: 'ghi-chu-ca-nhan',
        loadComponent: () => import('./private-note-list/private-note-list.component').then(m => m.PrivateNoteListComponent)
    },
    {
        path: 'lich-su',
        loadComponent: () => import('./histories-list/histories-list.component').then(m => m.HistoriesListComponent)
    }
]; 