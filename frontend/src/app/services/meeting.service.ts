import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { KeycloakService } from './keycloak/keycloak.service';

export interface MeetingResponse {
  joinId: number;
  meetingId: number;
  title: string;
  description: string;
  location: string;
  startTime: string;
  endTime: string;
  status: 'PENDING' | 'ACCEPTED' | 'REJECTED' | 'AUTHORIZED';
}

@Injectable({
  providedIn: 'root'
})
export class MeetingService {
  private baseUrl = 'http://localhost:9090/api/meeting';

  constructor(
    private http: HttpClient,
    private keycloakService: KeycloakService
  ) { }

  private formatDate(date: Date): string {
    return date.toISOString().split('T')[0];
  }

  getMeetingsForCalendar(startDate: Date, endDate: Date): Observable<MeetingResponse[]> {
    const userId = this.keycloakService.profile?.id;
    
    return this.http.get<MeetingResponse[]>(`${this.baseUrl}/get-for-user-calendar`, {
      params: {
        userId: userId || '',
        startDate: this.formatDate(startDate),
        endDate: this.formatDate(endDate)
      }
    });
  }
} 