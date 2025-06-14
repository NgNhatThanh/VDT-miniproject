import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { MeetingService } from '../../../services/meeting.service';
import { CommonModule } from '@angular/common';
import { MeetingManagementService, MeetingHistory } from '../../../services/meeting-management.service';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { InfiniteScrollDirective } from '../../../directives/infinite-scroll.directive';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-histories-list',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule,
    MatProgressSpinnerModule,
    InfiniteScrollDirective
  ],
  templateUrl: './histories-list.component.html',
  styles: [`
    @keyframes highlight {
      0% { background-color: rgba(59, 130, 246, 0.1); }
      100% { background-color: transparent; }
    }
    .highlight-new {
      animation: highlight 2s ease-out;
    }
  `]
})
export class HistoriesListComponent implements OnInit, OnDestroy {
  histories: MeetingHistory[] = [];
  limit: number = 15;
  offset: number = 0;
  isEnd: boolean = false;
  isLoading: boolean = false;
  latestMessageId: string | null = null;
  private subscription = new Subscription();

  constructor(
    private meetingService: MeetingService,
    private router: Router,
    private meetingManagementService: MeetingManagementService
  ) {}

  ngOnInit() {
    this.meetingService.currentMeeting$.subscribe(meeting => {
      if (!meeting) {
        this.router.navigate(['/lich-hop-ca-nhan']);
        return;
      }

      if (meeting.join.status !== 'ACCEPTED') {
        alert('Forbidden');
        window.history.back();
        return;
      }

      // Lắng nghe tin nhắn mới nhất
      this.subscription.add(
        this.meetingManagementService.latestHistoryMessage$.subscribe(message => {
          if (message && !this.isLoading) {
            this.histories = [message, ...this.histories];
            this.latestMessageId = message.id;
            setTimeout(() => {
              this.latestMessageId = null;
            }, 2000);
          }
        })
      );

      // Lấy lịch sử cuộc họp
      this.loadHistories(meeting.id);
    });
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  loadHistories(meetingId: number) {
    if (this.isEnd || this.isLoading) return;

    this.isLoading = true;
    this.meetingManagementService.getMeetingHistories(meetingId, this.limit, this.offset).subscribe({
      next: (data) => {
        if (data.length < this.limit) {
          this.isEnd = true;
        }
        this.histories = [...this.histories, ...data];
        this.offset += data.length;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading histories:', error);
        this.isLoading = false;
      }
    });
  }

  onScroll() {
    this.meetingService.currentMeeting$.subscribe(meeting => {
      if (meeting) {
        this.loadHistories(meeting.id);
      }
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
