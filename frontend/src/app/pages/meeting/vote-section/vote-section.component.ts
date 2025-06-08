import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MeetingManagementService, VotingInfo } from '../../../services/meeting-management.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-vote-section',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatIconModule, MatCardModule],
  templateUrl: './vote-section.component.html'
})
export class VoteSectionComponent implements OnInit, OnDestroy {
  votingList: VotingInfo[] = [];
  meetingId: number = 0;
  private subscription = new Subscription();

  constructor(
    private route: ActivatedRoute,
    private meetingService: MeetingManagementService
  ) {}

  ngOnInit() {
    // Lấy meetingId từ route params
    this.subscription.add(
      this.route.params.subscribe(params => {
        this.meetingId = params['meetingId'];
      })
    );

    // Lắng nghe sự thay đổi của trạng thái join meeting
    this.subscription.add(
      this.meetingService.meetingJoinStatus$.subscribe(isJoined => {
        if (isJoined) {
          this.loadVotingList();
        } else {
          this.votingList = [];
        }
      })
    );
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  private loadVotingList() {
    this.meetingService.getVotingList(this.meetingId).subscribe({
      next: (data) => {
        this.votingList = data;
      },
      error: (error) => {
        console.error('Lỗi khi lấy danh sách biểu quyết:', error);
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

  isVoteStarted(vote: VotingInfo): boolean {
    return new Date() >= new Date(vote.startTime);
  }

  isVoteEnded(vote: VotingInfo): boolean {
    return new Date() > new Date(vote.endTime);
  }
}
