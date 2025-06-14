import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MeetingManagementService, VotingInfo, MeetingHistory } from '../../../services/meeting-management.service';
import { Subscription } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { VoteModalComponent } from './vote-modal/vote-modal.component';
import { VoteResultModalComponent } from './vote-result-modal/vote-result-modal.component';
import { filter, debounceTime } from 'rxjs/operators';

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

    // Lắng nghe tin nhắn liên quan đến vote
    this.subscription.add(
      this.meetingService.latestHistoryMessage$.pipe(
        filter((message): message is MeetingHistory => 
          message !== null && this.shouldReloadVotingList(message)
        ),
        debounceTime(500) // Đợi 500ms sau tin nhắn cuối cùng
      ).subscribe(() => {
        this.loadVotingList();
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

  openVoteModal(vote: VotingInfo) {
    const meetingId = Number(this.route.snapshot.paramMap.get('meetingId'));
    const dialogRef = this.dialog.open(VoteModalComponent, {
      data: { 
        meetingId,
        meetingVoteId: vote.id 
      },
      width: '66vw',
      height: '80vh',
      maxWidth: '1200px',
      maxHeight: '800px',
      panelClass: 'vote-modal',
      disableClose: true
    });

    // Lắng nghe kết quả từ dialog
    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.loadVotingList(); // Tải lại danh sách vote khi submit thành công
      }
    });
  }

  openVoteResultModal(vote: VotingInfo) {
    const meetingId = Number(this.route.snapshot.paramMap.get('meetingId'));
    const dialogRef = this.dialog.open(VoteResultModalComponent, {
      data: { 
        meetingId,
        meetingVoteId: vote.id 
      },
      width: '75vw',
      height: '85vh',
      maxWidth: '1200px',
      maxHeight: '800px',
      panelClass: 'vote-result-modal'
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

  private shouldReloadVotingList(message: MeetingHistory): boolean {
    return message.type === 'VOTE_CREATED' || 
           message.type === 'VOTE_ENDED' || 
           message.type === 'VOTE_STARTED';
  }
}
