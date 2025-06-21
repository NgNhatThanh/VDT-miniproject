import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

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

export interface MeetingResponse {
  joinId: number;
  meetingId: number;
  title: string;
  description: string;
  location: string;
  startTime: string;
  endTime: string;
  status: 'PENDING' | 'ACCEPTED' | 'REJECTED' | 'DELEGATED';
}

export interface DocumentResponse {
  id: number;
  name: string;
  size: number;
  url: string;
}

@Injectable({
  providedIn: 'root'
})
export class MeetingService {
  private apiUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) { }

  getLocations(): Observable<MeetingLocation[]> {
    return this.http.get<MeetingLocation[]>(`${this.apiUrl}/meeting/location/all`);
  }

  addMeeting(meeting: CreateMeetingRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/meeting/create`, meeting);
  }

  uploadDocument(file: File): Observable<DocumentResponse> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post<DocumentResponse>(`${this.apiUrl}/document/upload`, formData).pipe(
      catchError(error => {
        console.error('Error uploading document:', error);
        throw error;
      })
    );
  }
} 