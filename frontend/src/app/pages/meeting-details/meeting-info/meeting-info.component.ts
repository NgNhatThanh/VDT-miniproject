import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { KeycloakService } from '../../../services/keycloak/keycloak.service';
import { MeetingService, MeetingDetails } from '../../../services/meeting.service';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { RejectReasonDialogComponent } from '../reject-reason-dialog.component';
import { ConfirmDialogComponent } from '../../../common/components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-meeting-info',
  standalone: true,
  imports: [CommonModule, MatIconModule, MatTooltipModule, MatButtonModule],
  templateUrl: './meeting-info.component.html',
  styles: ``
})
export class MeetingInfoComponent implements OnInit {
  meeting: MeetingDetails | null = null;
  rejectReason: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private meetingService: MeetingService,
    private KeycloakService: KeycloakService,
    private dialog: MatDialog
  ) {}

  ngOnInit() {
    this.route.parent?.params.subscribe(params => {
      const meetingId = params['meetingId'];
      if (meetingId) {
        const cached = this.meetingService['meetingCache'].get(meetingId);
        if (cached) {
          this.meeting = cached;
        } else {
          this.meetingService.getMeetingDetails(meetingId).subscribe(meeting => {
            this.meeting = meeting;
          });
        }
      }
    });
  }

  getUserFullName(){
    return this.KeycloakService.profile?.name;
  }

  onAcceptClick() {
    if (!this.meeting) return;
    
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Xác nhận tham dự',
        message: 'Bạn có chắc chắn muốn tham dự cuộc họp này không?'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.meetingService.updateMeetingJoin({
          joinId: this.meeting?.join?.id || 0,
          status: 'ACCEPTED'
        }).subscribe(updatedMeeting => {
          this.meeting = updatedMeeting;
        });
      }
    });
  }

  onRejectClick() {
    const dialogRef = this.dialog.open(RejectReasonDialogComponent, {
      width: '500px',
      disableClose: true
    });

    dialogRef.afterClosed().subscribe(reason => {
      if (reason && this.meeting) {
        this.meetingService.updateMeetingJoin({
          joinId: this.meeting.join.id,
          status: 'REJECTED',
          reason: reason
        }).subscribe(updatedMeeting => {
          this.meeting = updatedMeeting;
        });
      }
    });
  }
}
