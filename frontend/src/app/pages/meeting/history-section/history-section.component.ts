import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { MeetingManagementService, MeetingHistory } from '../../../services/meeting-management.service';
import { Subscription } from 'rxjs';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { InfiniteScrollDirective } from '../../../directives/infinite-scroll.directive';

@Component({
  selector: 'app-history-section',
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
  templateUrl: './history-section.component.html',
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
export class HistorySectionComponent implements OnInit, OnDestroy {
  histories: MeetingHistory[] = [];
  meetingId: number = 0;
  limit: number = 15;
  offset: number = 0;
  isEnd: boolean = false;
  isLoading: boolean = false;
  latestMessageId: string | null = null;
  private subscription = new Subscription();

  constructor(
    private meetingService: MeetingManagementService,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    // Lấy meetingId từ route params
    this.subscription.add(
      this.route.params.subscribe(params => {
        this.meetingId = params['meetingId'];
      })
    );

    // Lắng nghe sự thay đổi của meeting join status
    this.subscription.add(
      this.meetingService.meetingJoinStatus$.subscribe(isJoined => {
        if (isJoined) {
          this.resetAndLoadHistories();
        } else {
          this.resetHistories();
        }
      })
    );

    // Lắng nghe tin nhắn mới nhất
    this.subscription.add(
      this.meetingService.latestHistoryMessage$.subscribe(message => {
        if (message && !this.isLoading) {
          // Thêm tin nhắn mới vào đầu danh sách
          this.histories = [message, ...this.histories];
          // Lưu ID của tin nhắn mới nhất để highlight
          this.latestMessageId = message.id;
          // Reset ID sau 2 giây (thời gian animation)
          setTimeout(() => {
            this.latestMessageId = null;
          }, 2000);
        }
      })
    );
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  resetHistories() {
    this.histories = [];
    this.offset = 0;
    this.isEnd = false;
    this.isLoading = false;
    this.latestMessageId = null;
  }

  resetAndLoadHistories() {
    this.resetHistories();
    this.loadHistories();
  }

  loadHistories() {
    if (this.isEnd || this.isLoading) return;

    this.isLoading = true;
    this.meetingService.getMeetingHistories(this.meetingId, this.limit, this.offset).subscribe({
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
    this.loadHistories();
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
