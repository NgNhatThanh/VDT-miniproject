import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject, BehaviorSubject } from 'rxjs';
import { environment } from '../../environments/environment';
import { KeycloakService } from './keycloak/keycloak.service';
import { filter, tap } from 'rxjs/operators';
import { Client, Message, StompSubscription } from '@stomp/stompjs';

export interface Note {
  id: number;
  content: string;
  updatedAt: string;
} 

export interface Participant {
  joinId: number;
  fullName: string;
  picture: string;
  status: string;
  roles: MeetingRole[];
}

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

export interface MeetingHistory {
  id: string;
  content: string;
  type: string;
  createdAt: string;
}

export interface VotingOption {
  content: string;
}

export interface VotingQuestion {
  title: string;
  options: VotingOption[];
}

export interface CreateVotingRequest {
  meetingId: number;
  title: string;
  description: string;
  startTime: Date;
  endTime: Date;
  type: 'PUBLIC' | 'PRIVATE';
  documentIds: number[];
  questions: VotingQuestion[];
}

export interface VotingInfo {
  id: number;
  title: string;
  description: string;
  startTime: string;
  endTime: string;
  isVoted: boolean;
  type: 'PUBLIC' | 'PRIVATE';
}

export interface VoteOption {
  id: number;
  content: string;
  voters: any[] | null;
}

export interface VoteQuestion {
  id: number;
  title: string;
  options: VoteOption[];
}

export interface VoteDetail {
  id: number;
  title: string;
  description: string;
  type: 'PUBLIC' | 'PRIVATE';
  startTime: string;
  endTime: string;
  documents: MeetingDocument[];
  questions: VoteQuestion[];
}

export interface VoteAnswer {
  questionId: number;
  optionId: number;
}

export interface SubmitVoteRequest {
  meetingId: number;
  meetingVoteId: number;
  questionSelections: {
    questionId: number;
    optionId: number;
  }[];
}

export interface VoterInfo {
  fullName: string;
  picture: string;
}

export interface VoteOptionStatus {
  optionId: number;
  voteCount: number;
  voterInfos: VoterInfo[];
}

export interface VoteQuestionStatus {
  questionId: number;
  options: VoteOptionStatus[];
}

export interface VoteStatus {
  meetingVoteId: number;
  questions: VoteQuestionStatus[];
}

export interface VoteStatusResponse {
  detail: VoteDetail;
  vote: any;
  voteStatus: VoteStatus;
  voted: boolean;
}

export interface RegisterSpeechRequest {
  meetingId: number;
  content: string;
  duration: number;
}

export interface SpeechRegistration {
  id: number;
  content: string;
  duration: number;
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'ON_GOING' | 'ENDED';
  speakerFullName: string;
  createdAt: string;
}

export interface UpdateSpeechStatusRequest {
  speechId: number;
  meetingId: number;
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'ON_GOING' | 'ENDED';
}

export interface MeetingDocument {
  id: number;
  name: string;
  size: number;
  url: string;
}

export interface DocumentForApproval {
  meetingDocumentId: number;
  detail: MeetingDocument;
  uploaderFullName: string;
}

export interface UpdateDocumentStatusRequest {
  meetingDocumentId: number;
  meetingId: number;
  status: string;
}

@Injectable({
  providedIn: 'root'
})
export class MeetingManagementService {
  private apiUrl = environment.apiBaseUrl;
  private currentMeetingJoinInfo: MeetingJoinResponse | null = null;
  private meetingJoinStatus = new BehaviorSubject<boolean>(false);
  public meetingJoinStatus$ = this.meetingJoinStatus.asObservable();

  // Thêm BehaviorSubject cho meetingInfo
  private meetingInfo = new BehaviorSubject<MeetingHeaderInfo | null>(null);
  public meetingInfo$ = this.meetingInfo.asObservable();

  // STOMP Client
  private stompClient: Client | null = null;
  private connected = new BehaviorSubject<boolean>(false);
  public connected$ = this.connected.asObservable();
  private messageSubject = new Subject<any>();
  public message$ = this.messageSubject.asObservable();
  private subscription: StompSubscription | null = null;

  // Latest history message
  private latestHistoryMessage = new BehaviorSubject<MeetingHistory | null>(null);
  public latestHistoryMessage$ = this.latestHistoryMessage.asObservable();

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
    const url = `${this.apiUrl}/meeting/join?meetingId=${meetingId}`;
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
    return this.http.get<MeetingHeaderInfo>(`${this.apiUrl}/meeting/header-info?meetingId=${meetingId}`).pipe(
      tap(info => {
        this.meetingInfo.next(info);
      })
    );
  }

  // Lấy lịch sử cuộc họp
  getMeetingHistories(meetingId: number, limit: number = 10, offset: number = 0): Observable<MeetingHistory[]> {
    const url = `${this.apiUrl}/meeting-history/get-histories?meetingId=${meetingId}&limit=${limit}&offset=${offset}`;
    return this.http.get<MeetingHistory[]>(url);
  }

  // ===== STOMP methods =====
  connectWebsocket(meetingId: number) {
    if (this.stompClient) {
      this.disconnectWebsocket();
    }

    const token = this.keycloakService.keycloak.token;
    this.stompClient = new Client({
      brokerURL: `${this.apiUrl}/meeting-history/ws?token=${token}&meetingId=${meetingId}`,
      connectHeaders: {
        login: '',
        passcode: ''
      },
      debug: function (str) {
        console.log(str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000
    });

    this.stompClient.onConnect = (frame) => {
      console.log('Connected to STOMP');
      this.connected.next(true);
      
      // Subscribe to meeting history
      if (this.stompClient) {
        this.subscription = this.stompClient.subscribe(
          `/meeting/${meetingId}/history`,
          (message: Message) => {
            try {
              const data = JSON.parse(message.body);
              console.log(data);
              this.messageSubject.next(data);
              // Cập nhật tin nhắn mới nhất
              this.latestHistoryMessage.next(data);
            } catch (e) {
              this.messageSubject.next(message.body);
            }
          }
        );
      }
    };

    this.stompClient.onStompError = (frame) => {
      console.error('STOMP error:', frame);
      this.messageSubject.next({
        type: 'ERROR',
        message: 'Không thể xác thực kết nối. Vui lòng đăng nhập lại.'
      });
      this.disconnectWebsocket();
    };

    this.stompClient.activate();
  }

  disconnectWebsocket() {
    if (this.subscription) {
      this.subscription.unsubscribe();
      this.subscription = null;
    }
    
    if (this.stompClient) {
      this.stompClient.deactivate().then(() => {
        console.log('Disconnected from STOMP');
        this.connected.next(false);
      });
      this.stompClient = null;
    }
  }

  sendWebsocketMessage(msg: any) {
    if (this.stompClient && this.connected.value) {
      this.stompClient.publish({
        destination: '/app/meeting-history',
        body: JSON.stringify(msg)
      });
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

  // Tạo biểu quyết mới
  createVoting(voting: CreateVotingRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/vote/create-vote`, voting);
  }

  // Lấy danh sách biểu quyết của một cuộc họp
  getVotingList(meetingId: number): Observable<VotingInfo[]> {
    return this.http.get<VotingInfo[]>(`${this.apiUrl}/vote/get-list?meetingId=${meetingId}`);
  }

  // Lấy thông tin chi tiết biểu quyết để bỏ phiếu
  getVoteForSelection(meetingVoteId: number): Observable<VoteDetail> {
    return this.http.get<VoteDetail>(`${this.apiUrl}/vote/get-vote-for-selection?meetingVoteId=${meetingVoteId}`);
  }

  // Gửi phiếu bầu
  submitVote(request: SubmitVoteRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/vote/vote`, request);
  }

  // Lấy thông tin trạng thái biểu quyết
  getVoteStatus(meetingVoteId: number): Observable<VoteStatusResponse> {
    return this.http.get<VoteStatusResponse>(`${this.apiUrl}/vote/status?meetingVoteId=${meetingVoteId}`);
  }

  // Đăng ký phát biểu
  registerSpeech(request: RegisterSpeechRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/speech/register`, request);
  }

  // Lấy danh sách đăng ký phát biểu
  getSpeechRegistrations(meetingId: number): Observable<SpeechRegistration[]> {
    return this.http.get<SpeechRegistration[]>(`${this.apiUrl}/speech/get-list?meetingId=${meetingId}`);
  }

  // Cập nhật trạng thái phát biểu
  updateSpeechStatus(request: UpdateSpeechStatusRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/speech/update`, request);
  }

  // Lấy danh sách tài liệu của cuộc họp
  getMeetingDocuments(meetingId: number): Observable<MeetingDocument[]> {
    return this.http.get<MeetingDocument[]>(`${this.apiUrl}/meeting/document/get-list?meetingId=${meetingId}`);
  }

  // Thêm tài liệu vào cuộc họp
  addDocumentToMeeting(meetingId: number, documentId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/meeting/document/add-to-meeting?meetingId=${meetingId}&documentId=${documentId}`, {});
  }

  // Lấy danh sách tài liệu cần duyệt
  getDocumentsForApproval(meetingId: number): Observable<DocumentForApproval[]> {
    return this.http.get<DocumentForApproval[]>(`${this.apiUrl}/meeting/document/get-for-approvement?meetingId=${meetingId}`);
  }

  // Cập nhật trạng thái tài liệu
  updateDocumentStatus(request: UpdateDocumentStatusRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/meeting/document/update-status`, request);
  }

  getListNote(meetingId: number): Observable<Note[]> {
    return this.http.get<Note[]>(`${this.apiUrl}/meeting/get-list-note?meetingId=${meetingId}`);
  }

  addNote(meetingId: number, content: string): Observable<Note> {
    return this.http.post<Note>(`${this.apiUrl}/meeting/add-note`, {
      meetingId,
      content
    });
  }

  updateNote(noteId: number, content: string): Observable<Note> {
    return this.http.post<Note>(`${this.apiUrl}/meeting/update-note`, {
      noteId,
      content
    });
  }

  deleteNote(noteId: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/meeting/delete-note?noteId=${noteId}`, {});
  }

  // Lấy danh sách người tham gia cuộc họp
  getParticipants(meetingId: number): Observable<Participant[]> {
    return this.http.get<Participant[]>(`${this.apiUrl}/meeting/get-list-participants?meetingId=${meetingId}`);
  }
} 