import { Component, OnInit, OnDestroy } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { RegisterSpeechDialog } from './register-speech-dialog.component';
import { ActivatedRoute } from '@angular/router';
import { MeetingManagementService, SpeechRegistration, MeetingHistory } from '../../../services/meeting-management.service';
import { CommonModule } from '@angular/common';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Subscription } from 'rxjs';
import { debounceTime, filter } from 'rxjs/operators';
import { ConfirmDialogComponent } from '../../../common/components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-speech-section',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatIconModule, MatTooltipModule],
  templateUrl: './speech-section.component.html'
})
export class SpeechSectionComponent implements OnInit, OnDestroy {
  private meetingId!: number;
  speechRegistrations: SpeechRegistration[] = [];
  loading = true;
  private subscription = new Subscription();
  isHost = false;

  constructor(
    private dialog: MatDialog,
    private route: ActivatedRoute,
    private meetingService: MeetingManagementService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit() {
    this.subscription.add(
      this.route.params.subscribe(params => {
        this.meetingId = +params['meetingId'];
      })
    );

    // Lắng nghe sự thay đổi của meeting info từ service
    this.subscription.add(
      this.meetingService.meetingJoinStatus$.subscribe(isJoined => {
        if (isJoined) {
          this.checkUserRoles();
          this.loadSpeechRegistrations();
        } else {
          this.speechRegistrations = [];
          this.isHost = false;
        }
      })
    );

    // Lắng nghe tin nhắn mới nhất với debounceTime
    this.subscription.add(
      this.meetingService.latestHistoryMessage$.pipe(
        filter((message): message is MeetingHistory => 
          message !== null && this.shouldReloadRegistrations(message)
        ),
        debounceTime(500) // Đợi 500ms sau tin nhắn cuối cùng
      ).subscribe(() => {
        this.loadSpeechRegistrations();
      })
    );
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  private checkUserRoles() {
    const joinInfo = this.meetingService.getCurrentMeetingJoinInfo();
    if (joinInfo) {
      const roles = joinInfo.roles.map(role => role.name);
      this.isHost = roles.includes('HOST');
    }
  }

  private shouldReloadRegistrations(message: MeetingHistory): boolean {
    return message.type.startsWith('SPEECH');
  }

  loadSpeechRegistrations() {
    this.loading = true;
    this.meetingService.getSpeechRegistrations(this.meetingId).subscribe({
      next: (data) => {
        // Sắp xếp danh sách: REJECTED và ENDED xuống cuối
        this.speechRegistrations = data.sort((a, b) => {
          // Nếu cả hai đều là REJECTED hoặc ENDED, giữ nguyên thứ tự
          if ((a.status === 'REJECTED' || a.status === 'ENDED') && 
              (b.status === 'REJECTED' || b.status === 'ENDED')) {
            return 0;
          }
          // Nếu a là REJECTED hoặc ENDED, đẩy xuống cuối
          if (a.status === 'REJECTED' || a.status === 'ENDED') {
            return 1;
          }
          // Nếu b là REJECTED hoặc ENDED, đẩy xuống cuối
          if (b.status === 'REJECTED' || b.status === 'ENDED') {
            return -1;
          }
          // Các trường hợp còn lại giữ nguyên thứ tự
          return 0;
        });
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading speech registrations:', error);
        this.loading = false;
      }
    });
  }

  openRegisterDialog(): void {
    const dialogRef = this.dialog.open(RegisterSpeechDialog, {
      width: '500px',
      data: { 
        meetingId: this.meetingId,
        content: '', 
        duration: 5 
      },
      disableClose: true
    });
  }

  // Các phương thức xử lý cho HOST
  approveSpeech(speechId: number): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Xác nhận duyệt',
        message: 'Bạn có chắc chắn muốn duyệt phát biểu này không?'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.meetingService.updateSpeechStatus({
          speechId: speechId,
          status: 'APPROVED'
        }).subscribe({
          next: () => {
            this.snackBar.open('Đã duyệt phát biểu thành công', 'Đóng', {
              duration: 3000,
              horizontalPosition: 'center',
              verticalPosition: 'bottom'
            });
          },
          error: (error) => {
            console.error('Error approving speech:', error);
            this.snackBar.open('Có lỗi xảy ra khi duyệt phát biểu', 'Đóng', {
              duration: 3000,
              horizontalPosition: 'center',
              verticalPosition: 'bottom'
            });
          }
        });
      }
    });
  }

  rejectSpeech(speechId: number): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Xác nhận từ chối',
        message: 'Bạn có chắc chắn muốn từ chối phát biểu này không?'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.meetingService.updateSpeechStatus({
          speechId: speechId,
          status: 'REJECTED'
        }).subscribe({
          next: () => {
            this.snackBar.open('Đã từ chối phát biểu thành công', 'Đóng', {
              duration: 3000,
              horizontalPosition: 'center',
              verticalPosition: 'bottom'
            });
          },
          error: (error) => {
            console.error('Error rejecting speech:', error);
            this.snackBar.open('Có lỗi xảy ra khi từ chối phát biểu', 'Đóng', {
              duration: 3000,
              horizontalPosition: 'center',
              verticalPosition: 'bottom'
            });
          }
        });
      }
    });
  }

  startSpeech(speechId: number): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Xác nhận bắt đầu',
        message: 'Bạn có chắc chắn muốn bắt đầu phát biểu này không?'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.meetingService.updateSpeechStatus({
          speechId: speechId,
          status: 'ON_GOING'
        }).subscribe({
          next: () => {
            this.snackBar.open('Đã bắt đầu phát biểu thành công', 'Đóng', {
              duration: 3000,
              horizontalPosition: 'center',
              verticalPosition: 'bottom'
            });
          },
          error: (error) => {
            console.error('Error starting speech:', error);
            this.snackBar.open('Có lỗi xảy ra khi bắt đầu phát biểu', 'Đóng', {
              duration: 3000,
              horizontalPosition: 'center',
              verticalPosition: 'bottom'
            });
          }
        });
      }
    });
  }

  endSpeech(speechId: number): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Xác nhận kết thúc',
        message: 'Bạn có chắc chắn muốn kết thúc phát biểu này không?'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.meetingService.updateSpeechStatus({
          speechId: speechId,
          status: 'ENDED'
        }).subscribe({
          next: () => {
            this.snackBar.open('Đã kết thúc phát biểu thành công', 'Đóng', {
              duration: 3000,
              horizontalPosition: 'center',
              verticalPosition: 'bottom'
            });
          },
          error: (error) => {
            console.error('Error ending speech:', error);
            this.snackBar.open('Có lỗi xảy ra khi kết thúc phát biểu', 'Đóng', {
              duration: 3000,
              horizontalPosition: 'center',
              verticalPosition: 'bottom'
            });
          }
        });
      }
    });
  }

  getStatusColor(status: string): string {
    switch (status) {
      case 'PENDING':
        return 'bg-yellow-100 text-yellow-800';
      case 'APPROVED':
        return 'bg-green-100 text-green-800';
      case 'REJECTED':
        return 'bg-red-100 text-red-800';
      case 'ON_GOING':
        return 'bg-blue-100 text-blue-800';
      case 'ENDED':
        return 'bg-gray-100 text-gray-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleString('vi-VN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}
