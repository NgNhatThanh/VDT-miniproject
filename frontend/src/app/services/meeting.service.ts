import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, BehaviorSubject } from 'rxjs';
import { map, catchError, filter } from 'rxjs/operators';
import { KeycloakService } from './keycloak/keycloak.service';
import { environment } from '../../environments/environment';

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

export interface Location {
  id: number;
  name: string;
  description: string | null;
}

export interface Role {
  id: number;
  name: string;
  description: string | null;
}

export interface JoinInfo {
  id: number;
  status: string;
  roles: Role[];
}

export interface MeetingDetails {
  id: number;
  title: string;
  description: string;
  startTime: string;
  endTime: string;
  location: Location;
  join: JoinInfo;
}

export interface JoinUpdateRequest {
  joinId: number;
  status: 'PENDING' | 'ACCEPTED' | 'REJECTED' | 'DELEGATED';
  reason?: string;
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
  private baseUrl = `${environment.apiBaseUrl}/meeting`;
  private apiUrl = environment.apiBaseUrl;
  private meetingCache: Map<string, MeetingDetails> = new Map();
  public lastRejectReason: string | null = null;
  private currentMeetingSubject = new BehaviorSubject<MeetingDetails | null>(null);
  public currentMeeting$ = this.currentMeetingSubject.asObservable();
  private isLoadingMap: Map<string, boolean> = new Map();

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

  getMeetingDetails(meetingId: string): Observable<MeetingDetails> {
    // Kiểm tra cache trước
    const cachedMeeting = this.meetingCache.get(meetingId);
    if (cachedMeeting) {
      this.currentMeetingSubject.next(cachedMeeting);
      return of(cachedMeeting);
    }

    // Kiểm tra xem đang load chưa
    if (this.isLoadingMap.get(meetingId)) {
      return this.currentMeeting$.pipe(
        filter((meeting): meeting is MeetingDetails => 
          meeting !== null && meeting.id.toString() === meetingId
        )
      );
    }

    // Đánh dấu đang load
    this.isLoadingMap.set(meetingId, true);

    // Nếu không có trong cache, gọi API
    return this.http.get<MeetingDetails>(`${this.apiUrl}/meeting/${meetingId}`).pipe(
      map(meeting => {
        // Lưu vào cache
        this.meetingCache.set(meetingId, meeting);
        this.currentMeetingSubject.next(meeting);
        // Đánh dấu đã load xong
        this.isLoadingMap.set(meetingId, false);
        return meeting;
      }),
      catchError(error => {
        // Đánh dấu lỗi và reset trạng thái loading
        this.isLoadingMap.set(meetingId, false);
        console.error('Error fetching meeting details:', error);
        throw error;
      })
    );
  }

  updateMeetingJoin(request: JoinUpdateRequest): Observable<MeetingDetails> {
    return this.http.post<MeetingDetails>(`${this.apiUrl}/meeting/join-update`, request).pipe(
      map(meeting => {
        // Cập nhật cache với thông tin meeting mới
        const meetingId = meeting.id.toString();
        this.meetingCache.set(meetingId, meeting);
        this.currentMeetingSubject.next(meeting);
        return meeting;
      }),
      catchError(error => {
        console.error('Error updating meeting join:', error);
        throw error;
      })
    );
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