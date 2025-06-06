import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject, BehaviorSubject } from 'rxjs';
import { environment } from '../../environments/environment';
import { KeycloakService } from './keycloak/keycloak.service';
import { filter, tap } from 'rxjs/operators';
import * as Stomp from 'stompjs';

export interface MeetingManagement {
  id?: number;
  title: string;
  description: string;
  startTime: Date;
  endTime: Date;
  location: string;
  participants: string[];
  status: 'scheduled' | 'in-progress' | 'completed' | 'cancelled';
  organizer: string;
  meetingType: 'online' | 'offline';
  meetingLink?: string;
  notes?: string;
}

export interface MeetingRole {
  id: number;
  name: string;
  description: string | null;
}

export interface MeetingJoinResponse {
  id: number;
  status: string;
  roles: MeetingRole[];
}

export interface Location {
  id: number;
  name: string;
  description: string;
}

export interface MeetingInfo {
  title: string;
  description: string;
  startTime: string;
  endTime: string;
  location: Location;
}

export interface OnlineUser {
  id: string;
  fullName: string;
  picture: string;
  join: {
    id: number;
    status: string;
    roles: MeetingRole[];
  };
}

export interface MeetingHeaderInfo {
  info: MeetingInfo;
  onlineUsers: OnlineUser[];
}

@Injectable({
  providedIn: 'root'
})
export class MeetingManagementService {
  private apiUrl = `${environment.apiUrl}/meeting-management`;
  private currentMeetingJoinInfo: MeetingJoinResponse | null = null;
  private meetingJoinStatus = new BehaviorSubject<boolean>(false);
  public meetingJoinStatus$ = this.meetingJoinStatus.asObservable();

  // STOMP Client
  private stompClient: Stomp.Client | null = null;
  private connected = new BehaviorSubject<boolean>(false);
  public connected$ = this.connected.asObservable();
  private messageSubject = new Subject<any>();
  public message$ = this.messageSubject.asObservable();

  constructor(
    private http: HttpClient,
    private keycloakService: KeycloakService
  ) { }

  // Lấy danh sách tất cả cuộc họp
  getAllMeetings(): Observable<MeetingManagement[]> {
    return this.http.get<MeetingManagement[]>(this.apiUrl);
  }

  // Lấy thông tin một cuộc họp theo ID
  getMeetingById(id: number): Observable<MeetingManagement> {
    return this.http.get<MeetingManagement>(`${this.apiUrl}/${id}`);
  }

  // Tạo cuộc họp mới
  createMeeting(meeting: MeetingManagement): Observable<MeetingManagement> {
    return this.http.post<MeetingManagement>(this.apiUrl, meeting);
  }

  // Cập nhật thông tin cuộc họp
  updateMeeting(id: number, meeting: MeetingManagement): Observable<MeetingManagement> {
    return this.http.put<MeetingManagement>(`${this.apiUrl}/${id}`, meeting);
  }

  // Xóa cuộc họp
  deleteMeeting(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Cập nhật trạng thái cuộc họp
  updateMeetingStatus(id: number, status: MeetingManagement['status']): Observable<MeetingManagement> {
    return this.http.patch<MeetingManagement>(`${this.apiUrl}/${id}/status`, { status });
  }

  // Lấy danh sách cuộc họp theo người tổ chức
  getMeetingsByOrganizer(organizerId: string): Observable<MeetingManagement[]> {
    return this.http.get<MeetingManagement[]>(`${this.apiUrl}/organizer/${organizerId}`);
  }

  // Lấy danh sách cuộc họp theo người tham gia
  getMeetingsByParticipant(participantId: string): Observable<MeetingManagement[]> {
    return this.http.get<MeetingManagement[]>(`${this.apiUrl}/participant/${participantId}`);
  }

  // Thêm người tham gia vào cuộc họp
  addParticipant(meetingId: number, participantId: string): Observable<MeetingManagement> {
    return this.http.post<MeetingManagement>(`${this.apiUrl}/${meetingId}/participants`, { participantId });
  }

  // Xóa người tham gia khỏi cuộc họp
  removeParticipant(meetingId: number, participantId: string): Observable<MeetingManagement> {
    return this.http.delete<MeetingManagement>(`${this.apiUrl}/${meetingId}/participants/${participantId}`);
  }

  // Kiểm tra quyền tham gia meeting
  checkMeetingJoinPermission(meetingId: number): Observable<MeetingJoinResponse> {
    const url = `http://localhost:9090/api/meeting/join?meetingId=${meetingId}`;
    return this.http.get<MeetingJoinResponse>(url).pipe(
      tap(response => {
        if (!this.currentMeetingJoinInfo) {
          this.currentMeetingJoinInfo = response;
          this.meetingJoinStatus.next(true);
        }
      })
    );
  }

  // Lưu thông tin quyền tham gia meeting
  setCurrentMeetingJoinInfo(info: MeetingJoinResponse) {
    if (!this.currentMeetingJoinInfo) {
      this.currentMeetingJoinInfo = info;
      this.meetingJoinStatus.next(true);
    }
  }

  // Reset trạng thái join meeting
  resetMeetingJoinStatus() {
    this.currentMeetingJoinInfo = null;
    this.meetingJoinStatus.next(false);
  }

  // Lấy thông tin quyền tham gia meeting hiện tại
  getCurrentMeetingJoinInfo(): MeetingJoinResponse | null {
    return this.currentMeetingJoinInfo;
  }

  // Lấy thông tin header của cuộc họp
  getMeetingHeaderInfo(meetingId: number): Observable<MeetingHeaderInfo> {
    return this.http.get<MeetingHeaderInfo>(`http://localhost:9090/api/meeting/header-info?meetingId=${meetingId}`);
  }

  // ===== STOMP methods =====
  connectWebsocket(meetingId: number) {
    if (this.stompClient) {
      this.disconnectWebsocket();
    }

    const token = this.keycloakService.keycloak.token;
    this.stompClient = Stomp.client(`http://localhost:9090/api/meeting-history/ws?token=${token}&meetingId=${meetingId}`)
    this.stompClient.connect(
      { login: '', passcode: '' },
      (frame?: Stomp.Frame) => {
        console.log('Connected to STOMP');
        this.connected.next(true);
        
        // Subscribe to meeting history
        this.stompClient?.subscribe(
          `/meeting/${meetingId}/history`,
          (message: Stomp.Message) => {
            try {
              const data = JSON.parse(message.body);
              console.log(data);
              this.messageSubject.next(data);
            } catch (e) {
              this.messageSubject.next(message.body);
            }
          }
        );
      },
      (error: string | Stomp.Frame) => {
        console.error('STOMP error:', error);
        this.messageSubject.next({
          type: 'ERROR',
          message: 'Không thể xác thực kết nối. Vui lòng đăng nhập lại.'
        });
        this.disconnectWebsocket();
      }
    );
  }

  disconnectWebsocket() {
    if (this.stompClient) {
      this.stompClient.disconnect(() => {
        console.log('Disconnected from STOMP');
        this.connected.next(false);
      });
      this.stompClient = null;
    }
  }

  sendWebsocketMessage(msg: any) {
    if (this.stompClient && this.connected.value) {
      this.stompClient.send('/app/meeting-history', {}, JSON.stringify(msg));
    }
  }

  // Subscribe vào tất cả messages
  subscribeToMessages(): Observable<any> {
    return this.message$;
  }

  // Subscribe vào messages theo type
  subscribeToMessageType(type: string): Observable<any> {
    return this.message$.pipe(
      filter(message => message.type === type)
    );
  }

  // Subscribe vào messages của một meeting cụ thể
  subscribeToMeeting(meetingId: number): Observable<any> {
    return this.message$.pipe(
      filter(message => message.meetingId === meetingId)
    );
  }

  // Subscribe vào messages lỗi
  subscribeToErrors(): Observable<any> {
    return this.message$.pipe(
      filter(message => message.type === 'ERROR')
    );
  }

  // Subscribe vào channel meeting history
  subscribeToMeetingHistory(meetingId: number): Observable<any> {
    return this.message$.pipe(
      filter(message => message.channel === `/api/meeting-history/meeting/${meetingId}`)
    );
  }

} 