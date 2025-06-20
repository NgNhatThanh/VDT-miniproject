import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MeetingRole } from '../../models/meeting-role.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MeetingRoleService {
  private apiUrl = `${environment.apiBaseUrl}/meeting/role`;

  constructor(private http: HttpClient) { }

  getAllRoles(): Observable<MeetingRole[]> {
    return this.http.get<MeetingRole[]>(`${this.apiUrl}/all`);
  }
} 