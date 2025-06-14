import { Component, Input, Output, EventEmitter, OnInit, OnDestroy } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { NgIf } from '@angular/common';
import { MatBadgeModule } from '@angular/material/badge';
import { MeetingManagementService, MeetingHistory } from '../../../../services/meeting-management.service';
import { Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';
import { MatDialog } from '@angular/material/dialog';
import { DocumentApprovalModalComponent } from './document-approval-modal/document-approval-modal.component';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-document-approval-button',
  templateUrl: './document-approval-button.component.html',
  standalone: true,
  imports: [MatButtonModule, MatIconModule, NgIf, MatBadgeModule]
})
export class DocumentApprovalButtonComponent implements OnInit, OnDestroy {
  @Input() isDocumentApprover: boolean = false;
  @Output() approveDocument = new EventEmitter<void>();
  
  showBadge: boolean = false;
  private subscription = new Subscription();
  private meetingId: number;

  constructor(
    private meetingService: MeetingManagementService,
    private dialog: MatDialog,
    private route: ActivatedRoute
  ) {
    this.meetingId = Number(this.route.snapshot.paramMap.get('meetingId'));
  }

  ngOnInit() {
    this.subscription.add(
      this.meetingService.latestHistoryMessage$.pipe(
        filter((message): message is MeetingHistory => 
          message !== null && message.type === 'DOCUMENT_UPLOADED'
        )
      ).subscribe(() => {
        this.showBadge = true;
      })
    );
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  onApproveDocument() {
    const dialogRef = this.dialog.open(DocumentApprovalModalComponent, {
      width: '50vw',
      height: '80vh',
      disableClose: true,
      data: { meetingId: this.meetingId }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.showBadge = false;
        this.approveDocument.emit();
      }
    });
  }
} 