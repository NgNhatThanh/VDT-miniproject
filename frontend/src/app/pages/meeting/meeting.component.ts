import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MeetingManagementService, MeetingJoinResponse } from '../../services/meeting-management.service';
import { HeaderComponent } from './header/header.component';
import { ManageSectionComponent } from './manage-section/manage-section.component';
import { HistorySectionComponent } from './history-section/history-section.component';
import { DocumentSectionComponent } from './document-section/document-section.component';
import { SpeechSectionComponent } from './speech-section/speech-section.component';
import { VoteSectionComponent } from './vote-section/vote-section.component';

@Component({
  selector: 'app-meeting',
  templateUrl: './meeting.component.html',
  imports: [
    HeaderComponent,
    ManageSectionComponent,
    HistorySectionComponent,
    DocumentSectionComponent,
    SpeechSectionComponent,
    VoteSectionComponent
  ]
})
export class MeetingComponent implements OnInit {
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private meetingService: MeetingManagementService
  ) {
    console.log('MeetingComponent constructor called');
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      const meetingId = params['meetingId'];
      this.checkMeetingPermission(meetingId);
    });
  }

  private checkMeetingPermission(meetingId: number) {
    this.meetingService.checkMeetingJoinPermission(meetingId).subscribe({
      next: (response: MeetingJoinResponse) => {
        this.meetingService.setCurrentMeetingJoinInfo(response);
        this.meetingService.connectWebsocket(meetingId);
      },
      error: (error) => {
        if (error.status === 400) {
          alert('Bạn không có quyền tham gia cuộc họp này!');
          this.router.navigate(['/']);
        }
      }
    });
  }
}
