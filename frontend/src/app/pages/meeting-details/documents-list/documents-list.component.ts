import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MeetingService } from '../../../services/meeting.service';
import { CommonModule } from '@angular/common';
import { MeetingManagementService, MeetingDocument } from '../../../services/meeting-management.service';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { DocumentCardComponent } from '../../../components/document-card/document-card.component';

@Component({
  selector: 'app-documents-list',
  standalone: true,
  imports: [CommonModule, MatIconModule, MatProgressSpinnerModule, DocumentCardComponent],
  templateUrl: './documents-list.component.html'
})
export class DocumentsListComponent implements OnInit {
  documents: MeetingDocument[] = [];
  isLoading: boolean = false;

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

      // Lấy danh sách tài liệu
      this.isLoading = true;
      this.meetingManagementService.getMeetingDocuments(meeting.id).subscribe({
        next: (documents) => {
          this.documents = documents;
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Lỗi khi lấy danh sách tài liệu:', error);
          this.isLoading = false;
        }
      });
    });
  }

  // TrackBy function để tối ưu hiệu suất rendering
  trackByDocumentId(index: number, document: MeetingDocument): number {
    return document.id;
  }
}
