import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MeetingService } from '../../../services/meeting.service';
import { CommonModule } from '@angular/common';
import { MeetingManagementService, Participant } from '../../../services/meeting-management.service';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-participants-list',
  standalone: true,
  imports: [CommonModule, MatIconModule],
  templateUrl: './participants-list.component.html',
  styles: ``
})
export class ParticipantsListComponent implements OnInit {
  participants: Participant[] = [];

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

      // Lấy danh sách người tham gia
      this.meetingManagementService.getParticipants(meeting.id).subscribe(
        participants => {
          this.participants = participants;
        },
        error => {
          console.error('Lỗi khi lấy danh sách người tham gia:', error);
        }
      );
    });
  }
}
