import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogModule, MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MeetingManagementService, DocumentForApproval } from '../../../../../services/meeting-management.service';
import { DocumentCardComponent } from '../../../../../components/document-card/document-card.component';
import { ConfirmDialogComponent } from '../../../../../common/components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-document-approval-modal',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule, MatIconModule, DocumentCardComponent, MatSnackBarModule],
  templateUrl: './document-approval-modal.component.html'
})
export class DocumentApprovalModalComponent implements OnInit {
  documents: DocumentForApproval[] = [];
  loading: boolean = true;

  constructor(
    public dialogRef: MatDialogRef<DocumentApprovalModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { meetingId: number },
    private meetingService: MeetingManagementService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {}

  ngOnInit() {
    this.loadDocuments();
  }

  loadDocuments() {
    this.loading = true;
    this.meetingService.getDocumentsForApproval(this.data.meetingId).subscribe({
      next: (documents) => {
        this.documents = documents;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading documents:', error);
        this.loading = false;
        this.snackBar.open('Không thể tải danh sách tài liệu', 'Đóng', {
          duration: 3000
        });
      }
    });
  }

  onApprove(documentId: number): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Xác nhận phê duyệt',
        message: 'Bạn có chắc chắn muốn phê duyệt tài liệu này?'
      }
    });

    dialogRef.afterClosed().subscribe((result: boolean) => {
      if (result) {
        this.meetingService.updateDocumentStatus({
          meetingDocumentId: documentId,
          meetingId: this.data.meetingId,
          status: 'APPROVED'
        }).subscribe({
          next: () => {
            this.documents = this.documents.filter(doc => doc.meetingDocumentId !== documentId);
            this.snackBar.open('Đã phê duyệt tài liệu thành công', 'Đóng', {
              duration: 3000
            });
          },
          error: (error) => {
            console.error('Error approving document:', error);
            this.snackBar.open('Không thể phê duyệt tài liệu', 'Đóng', {
              duration: 3000
            });
          }
        });
      }
    });
  }

  onReject(documentId: number): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Xác nhận từ chối',
        message: 'Bạn có chắc chắn muốn từ chối tài liệu này?'
      }
    });

    dialogRef.afterClosed().subscribe((result: boolean) => {
      if (result) {
        this.meetingService.updateDocumentStatus({
          meetingDocumentId: documentId,
          meetingId: this.data.meetingId,
          status: 'REJECTED'
        }).subscribe({
          next: () => {
            this.documents = this.documents.filter(doc => doc.meetingDocumentId !== documentId);
            this.snackBar.open('Đã từ chối tài liệu thành công', 'Đóng', {
              duration: 3000
            });
          },
          error: (error) => {
            console.error('Error rejecting document:', error);
            this.snackBar.open('Không thể từ chối tài liệu', 'Đóng', {
              duration: 3000
            });
          }
        });
      }
    });
  }

  onClose(): void {
    this.dialogRef.close(true);
  }
} 