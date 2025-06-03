import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MeetingService, MeetingDetails } from '../../services/meeting.service';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';

@Component({
  selector: 'app-meeting-details',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatTabsModule
  ],
  templateUrl: './meeting-details.component.html',
  styles: ``
})
export class MeetingDetailsComponent implements OnInit {
  meeting: MeetingDetails | null = null;
  loading = true;
  error: string | null = null;
  tabs = [
    { name: 'Thông tin', path: 'thong-tin' },
    { name: 'Thành phần tham dự', path: 'thanh-phan-tham-du' }
  ];

  constructor(
    private route: ActivatedRoute,
    private meetingService: MeetingService,
  ) {}

  ngOnInit() {
    this.route.params.subscribe(params => {
      const meetingId = params['meetingId'];
      if (meetingId) {
        this.loadMeetingDetails(meetingId);
      }
    });

    // Subscribe để nhận thông tin cập nhật từ service
    this.meetingService.currentMeeting$.subscribe(meeting => {
      if (meeting) {
        this.meeting = meeting;
      }
    });
  }

  private loadMeetingDetails(meetingId: string) {
    this.loading = true;
    this.error = null;

    this.meetingService.getMeetingDetails(meetingId).subscribe({
      next: (meeting) => {
        this.meeting = meeting;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Không thể tải thông tin cuộc họp. Vui lòng thử lại sau.';
        this.loading = false;
        console.error('Error loading meeting details:', err);
      }
    });
  }

  get isOngoing(): boolean {
    if (!this.meeting) return false;
    const now = new Date();
    const start = new Date(this.meeting.startTime);
    const end = new Date(this.meeting.endTime);
    return now >= start && now <= end;
  }

  get canJoinMeeting(): boolean {
    return this.isOngoing && this.meeting?.join?.status === 'ACCEPTED';
  }

  goBack() {
    window.history.back();
  }

  joinMeeting() {
    // TODO: Thực hiện logic tham gia phòng họp ở đây
    alert('Chức năng vào phòng họp sẽ được bổ sung sau!');
  }
}
