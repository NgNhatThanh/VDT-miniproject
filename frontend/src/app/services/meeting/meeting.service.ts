import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface MeetingLocation {
  id: number;
  name: string;
  description: string;
}

export interface MeetingJoin {
  userId: string;
  roleId: number;
}

export interface CreateMeetingRequest {
  title: string;
  description: string;
  startTime: string;
  endTime: string;
  locationId: number;
  joins: MeetingJoin[];
  documentIds: number[];
}

@Injectable({
  providedIn: 'root'
})
export class MeetingService {
  private apiUrl = 'http://localhost:9090/api/meeting';

  constructor(private http: HttpClient) { }

  getLocations(): Observable<MeetingLocation[]> {
    return this.http.get<MeetingLocation[]>(`${this.apiUrl}/location/all`);
  }

  addMeeting(meeting: CreateMeetingRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/create`, meeting);
  }
} 