import { Component, Inject, OnInit, OnDestroy, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MeetingManagementService, VoteStatusResponse, MeetingHistory } from '../../../../services/meeting-management.service';
import { VoterListModalComponent } from './voter-list-modal/voter-list-modal.component';
import { Subscription } from 'rxjs';
import { filter, debounceTime, skip } from 'rxjs/operators';

interface VoteSelection {
  questionId: number;
  optionId: number;
}

@Component({
  selector: 'app-vote-result-modal',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule, MatIconModule, MatTooltipModule],
  templateUrl: 'vote-result-modal.component.html'
})
export class VoteResultModalComponent implements OnInit, OnDestroy {
  @ViewChild('contentContainer') contentContainer!: ElementRef;
  
  voteData: VoteStatusResponse | null = null;
  loading = true;
  error: string | null = null;
  lastUpdated: Date | null = null;
  private subscription = new Subscription();
  private scrollPosition = 0;

  constructor(
    public dialogRef: MatDialogRef<VoteResultModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { meetingId: number, meetingVoteId: number },
    private meetingService: MeetingManagementService,
    private dialog: MatDialog
  ) {}

  ngOnInit() {
    this.loadVoteData();

    // Lắng nghe tin nhắn mới nhất với debounceTime
    this.subscription.add(
      this.meetingService.latestHistoryMessage$.pipe(
        filter((message): message is MeetingHistory => 
          message !== null && message.type === 'VOTE_VOTED'
        ),
        debounceTime(500), // Đợi 500ms sau tin nhắn cuối cùng
      ).subscribe(() => {
        // Lưu vị trí thanh cuộn hiện tại
        if (this.contentContainer) {
          this.scrollPosition = this.contentContainer.nativeElement.scrollTop;
        }
        this.loadVoteData();
      })
    );
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  private loadVoteData() {
    this.loading = true;
    this.error = null;
    
    this.meetingService.getVoteStatus(this.data.meetingVoteId).subscribe({
      next: (data) => {
        this.voteData = data;
        this.loading = false;
        this.lastUpdated = new Date();
        
        // Khôi phục vị trí thanh cuộn sau khi dữ liệu được cập nhật
        setTimeout(() => {
          if (this.contentContainer) {
            this.contentContainer.nativeElement.scrollTop = this.scrollPosition;
          }
        });
      },
      error: (err) => {
        this.error = 'Không thể tải dữ liệu biểu quyết';
        this.loading = false;
        console.error('Lỗi khi tải dữ liệu biểu quyết:', err);
      }
    });
  }

  isVoteStarted(): boolean {
    if (!this.voteData?.detail) return false;
    return new Date() >= new Date(this.voteData.detail.startTime);
  }

  isVoteEnded(): boolean {
    if (!this.voteData?.detail) return false;
    return new Date() > new Date(this.voteData.detail.endTime);
  }

  getVoteCount(questionId: number, optionId: number): number {
    if (!this.voteData?.voteStatus) return 0;
    
    const question = this.voteData.voteStatus.questions.find(q => q.questionId === questionId);
    if (!question) return 0;
    
    const option = question.options.find(o => o.optionId === optionId);
    return option?.voteCount || 0;
  }

  getTotalVotesForQuestion(questionId: number): number {
    if (!this.voteData?.voteStatus) return 0;
    
    const question = this.voteData.voteStatus.questions.find(q => q.questionId === questionId);
    if (!question) return 0;
    
    return question.options.reduce((total, option) => total + option.voteCount, 0);
  }

  getVotePercentage(questionId: number, optionId: number): number {
    const totalVotes = this.getTotalVotesForQuestion(questionId);
    if (totalVotes === 0) return 0;
    
    const voteCount = this.getVoteCount(questionId, optionId);
    return (voteCount / totalVotes) * 100;
  }

  getVoterInfos(questionId: number, optionId: number) {
    if (!this.voteData?.voteStatus) return [];
    
    const question = this.voteData.voteStatus.questions.find(q => q.questionId === questionId);
    if (!question) return [];
    
    const option = question.options.find(o => o.optionId === optionId);
    return option?.voterInfos || [];
  }

  isUserVoted(questionId: number, optionId: number): boolean {
    if (!this.voteData?.vote?.selections) return false;
    return this.voteData.vote.selections.some(
      (selection: VoteSelection) => selection.questionId === questionId && selection.optionId === optionId
    );
  }

  showVoters(questionId: number, optionId: number) {
    const voters = this.getVoterInfos(questionId, optionId);
    this.dialog.open(VoterListModalComponent, {
      data: { voters },
      width: '400px',
      maxHeight: '80vh'
    });
  }

  formatDateTime(date: Date): string {
    return date.toLocaleString('vi-VN', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    });
  }
} 