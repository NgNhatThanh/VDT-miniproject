import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MeetingManagementService, MeetingHeaderInfo } from '../../../services/meeting-management.service';
import { LeaveMeetingModalComponent } from './leave-meeting-modal/leave-meeting-modal.component';
import { ParticipantsModalComponent } from './participants-modal/participants-modal.component';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatIconModule],
  templateUrl: './header.component.html'
})
export class HeaderComponent implements OnInit, OnDestroy {
  meetingInfo: MeetingHeaderInfo | null = null;
  meetingId: number = 0;
  private subscription = new Subscription();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private meetingService: MeetingManagementService,
    private dialog: MatDialog
  ) {}

  ngOnInit() {
    // Lấy meetingId từ route params
    this.subscription.add(
      this.route.params.subscribe(params => {
        this.meetingId = params['meetingId'];
      })
    );

    // Lắng nghe sự thay đổi của meeting info từ service
    this.subscription.add(
      this.meetingService.meetingJoinStatus$.subscribe(isJoined => {
        if (isJoined) {
          this.meetingService.getMeetingHeaderInfo(this.meetingId).subscribe({
            next: (data) => {
              this.meetingInfo = data;
            },
            error: (error) => {
              console.error('Error loading meeting info:', error);
            }
          });
        } else {
          this.meetingInfo = null;
        }
      })
    );
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  openLeaveModal() {
    const dialogRef = this.dialog.open(LeaveMeetingModalComponent, {
      width: '400px'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === 'leave') {
        this.meetingService.resetMeetingJoinStatus();
        window.location.assign("/");
      }
    });
  }

  openParticipantsModal() {
    const dialogRef = this.dialog.open(ParticipantsModalComponent, {
      width: '600px',
      data: { participants: this.meetingInfo?.onlineUsers || [] }
    });
  }

  formatDateTime(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleString('vi-VN', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}
