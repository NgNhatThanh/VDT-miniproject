import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { MeetingManagementService, MeetingDocument, MeetingHistory } from '../../../services/meeting-management.service';
import { DocumentCardComponent } from '../../../components/document-card/document-card.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Subscription } from 'rxjs';
import { debounceTime, filter } from 'rxjs/operators';
import { MeetingService } from '../../../services/meeting.service';

@Component({
  selector: 'app-document-section',
  imports: [CommonModule, DocumentCardComponent, MatProgressSpinnerModule, MatIconModule, MatButtonModule],
  templateUrl: './document-section.component.html',
  styles: `
    /* Custom scrollbar for better UX */
    .custom-scrollbar::-webkit-scrollbar {
      width: 6px;
    }
    .custom-scrollbar::-webkit-scrollbar-track {
      background: #f1f1f1;
      border-radius: 3px;
    }
    .custom-scrollbar::-webkit-scrollbar-thumb {
      background: #c1c1c1;
      border-radius: 3px;
    }
    .custom-scrollbar::-webkit-scrollbar-thumb:hover {
      background: #a8a8a8;
    }
  `
})
export class DocumentSectionComponent implements OnInit, OnDestroy {
  documents: MeetingDocument[] = [];
  meetingId: number = 0;
  isLoading: boolean = false;
  isUploading: boolean = false;
  private subscription = new Subscription();

  constructor(
    private route: ActivatedRoute,
    private meetingService: MeetingManagementService,
    private meetingApiService: MeetingService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit() {
    // Lấy meetingId từ route params
    this.subscription.add(
      this.route.params.subscribe(params => {
        this.meetingId = params['meetingId'];
      })
    );

    // Lắng nghe sự thay đổi join status từ service
    this.subscription.add(
      this.meetingService.meetingJoinStatus$.subscribe(isJoined => {
        if (isJoined) {
          this.loadDocuments();
        } else {
          this.documents = [];
        }
      })
    );

    // Lắng nghe tin nhắn mới nhất với debounceTime
    this.subscription.add(
      this.meetingService.latestHistoryMessage$.pipe(
        filter((message): message is MeetingHistory => 
          message !== null && this.shouldReloadDocuments(message)
        ),
        debounceTime(500) // Đợi 500ms sau tin nhắn cuối cùng
      ).subscribe(() => {
        this.loadDocuments();
      })
    );
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  private shouldReloadDocuments(message: MeetingHistory): boolean {
    return message.type === 'DOCUMENT_APPROVED';
  }

  private loadDocuments() {
    if (!this.meetingId) return;
    
    this.isLoading = true;
    this.meetingService.getMeetingDocuments(this.meetingId).subscribe({
      next: (data) => {
        this.documents = data;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Lỗi khi tải danh sách tài liệu:', error);
        this.isLoading = false;
      }
    });
  }

  // Helper methods cho template
  formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  }

  downloadDocument(document: MeetingDocument) {
    window.open(document.url, '_blank');
  }

  // TrackBy function để tối ưu hiệu suất rendering
  trackByDocumentId(index: number, document: MeetingDocument): number {
    return document.id;
  }

  // Xử lý upload tài liệu
  onUploadDocument() {
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = '.pdf,.doc,.docx,.xls,.xlsx,.ppt,.pptx';
    
    input.onchange = (event: any) => {
      const file = event.target.files[0];
      if (file) {
        this.isUploading = true;
        this.meetingApiService.uploadDocument(file).subscribe({
          next: (response) => {
            // Sau khi upload thành công, thêm tài liệu vào cuộc họp
            this.meetingService.addDocumentToMeeting(this.meetingId, response.id).subscribe({
              next: () => {
                this.snackBar.open('Tài liệu đăng tải thành công, đang chờ được phê duyệt', 'Đóng', {
                  duration: 5000,
                  horizontalPosition: 'center',
                  verticalPosition: 'top'
                });
                this.isUploading = false;
              },
              error: (error) => {
                console.error('Lỗi khi thêm tài liệu vào cuộc họp:', error);
                this.snackBar.open('Có lỗi xảy ra khi thêm tài liệu vào cuộc họp', 'Đóng', {
                  duration: 5000,
                  horizontalPosition: 'center',
                  verticalPosition: 'top'
                });
                this.isUploading = false;
              }
            });
          },
          error: (error) => {
            console.error('Lỗi khi upload tài liệu:', error);
            this.snackBar.open('Có lỗi xảy ra khi tải lên tài liệu', 'Đóng', {
              duration: 5000,
              horizontalPosition: 'center',
              verticalPosition: 'top'
            });
            this.isUploading = false;
          }
        });
      }
    };
    
    input.click();
  }
}
