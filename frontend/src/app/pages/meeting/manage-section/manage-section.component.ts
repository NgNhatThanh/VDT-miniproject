import { Component, OnInit, OnDestroy } from '@angular/core';
import { MeetingManagementService } from '../../../services/meeting-management.service';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Subscription } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { CreateVotingDialogComponent } from './create-voting-dialog/create-voting-dialog.component';
import { ActivatedRoute } from '@angular/router';
import { NgIf } from '@angular/common';
import { DocumentApprovalButtonComponent } from './document-approval-button/document-approval-button.component';
import { PrivateNoteModalComponent } from './private-note-modal/private-note-modal.component';

@Component({
  selector: 'app-manage-section',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatIconModule, NgIf, DocumentApprovalButtonComponent],
  templateUrl: './manage-section.component.html',
  styles: [`
    button {
      border-radius: 5px;
    }
  `]
})
export class ManageSectionComponent implements OnInit, OnDestroy {
  isHost = false;
  isDocumentApprover = false;
  private subscription = new Subscription();
  private meetingId: number = 0;

  constructor(
    private meetingService: MeetingManagementService, 
    private dialog: MatDialog,
    private route: ActivatedRoute
  ) {
    this.route.params.subscribe(params => {
      this.meetingId = +params['meetingId'];
    });
  }

  ngOnInit() {
    // Lắng nghe sự thay đổi của meeting join status
    this.subscription.add(
      this.meetingService.meetingJoinStatus$.subscribe(isJoined => {
        if (isJoined) {
          this.checkUserRoles();
        } else {
          this.isHost = false;
          this.isDocumentApprover = false;
        }
      })
    );
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  private checkUserRoles() {
    const joinInfo = this.meetingService.getCurrentMeetingJoinInfo();
    if (joinInfo) {
      const roles = joinInfo.roles.map(role => role.name);
      console.log("roles", roles);
      this.isHost = roles.includes('HOST');
      this.isDocumentApprover = roles.includes('DOCUMENT_APPROVER');
    }
  }

  addPersonalNote() {
    const dialogRef = this.dialog.open(PrivateNoteModalComponent, {
      width: '500px',
      disableClose: true,
      data: { meetingId: this.meetingId }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        console.log('Note content:', result);
        // TODO: Xử lý lưu ghi chú ở đây
      }
    });
  }

  startVoting(): void {
    const dialogRef = this.dialog.open(CreateVotingDialogComponent, {
      width: '600px',
      disableClose: true,
      data: { meetingId: this.meetingId }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        console.log('Voting data:', result);
        // TODO: Xử lý dữ liệu biểu quyết ở đây
      }
    });
  }

  approveDocument() {
    // TODO: Implement
  }
}
