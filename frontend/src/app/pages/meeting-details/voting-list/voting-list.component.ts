import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatDialog } from '@angular/material/dialog';
import { MeetingService } from '../../../services/meeting.service';
import { MeetingManagementService, VotingInfo } from '../../../services/meeting-management.service';
import { VoteResultModalComponent } from '../../meeting/vote-section/vote-result-modal/vote-result-modal.component';

@Component({
  selector: 'app-voting-list',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatIconModule, MatCardModule],
  templateUrl: './voting-list.component.html',
  styles: ``
})
export class VotingListComponent implements OnInit {
  votingList: VotingInfo[] = [];

  constructor(
    private meetingService: MeetingService,
    private router: Router,
    private meetingManagementService: MeetingManagementService,
    private dialog: MatDialog
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

      // Lấy danh sách biểu quyết
      this.meetingManagementService.getVotingList(meeting.id).subscribe({
        next: (data) => {
          this.votingList = data;
        },
        error: (error) => {
          console.error('Lỗi khi lấy danh sách biểu quyết:', error);
        }
      });
    });
  }

  openVoteResultModal(vote: VotingInfo) {
    this.meetingService.currentMeeting$.subscribe(meeting => {
      if (!meeting) return;

      const dialogRef = this.dialog.open(VoteResultModalComponent, {
        data: { 
          meetingId: meeting.id,
          meetingVoteId: vote.id 
        },
        width: '75vw',
        height: '85vh',
        maxWidth: '1200px',
        maxHeight: '800px',
        panelClass: 'vote-result-modal'
      });
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
