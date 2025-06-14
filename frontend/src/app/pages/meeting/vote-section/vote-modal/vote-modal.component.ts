import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';
import { MeetingManagementService, VoteDetail, SubmitVoteRequest } from '../../../../services/meeting-management.service';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatRadioModule } from '@angular/material/radio';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';
import { ConfirmDialogComponent } from '../../../../common/components/confirm-dialog/confirm-dialog.component';
import { MatSnackBar } from '@angular/material/snack-bar';
import { DocumentCardComponent } from '../../../../components/document-card/document-card.component';

interface VoteModalData {
  meetingId: number;
  meetingVoteId: number;
}

@Component({
  selector: 'app-vote-modal',
  templateUrl: './vote-modal.component.html',
  standalone: true,
  imports: [
    CommonModule,
    MatProgressSpinnerModule,
    MatRadioModule,
    MatButtonModule,
    MatIconModule,
    DocumentCardComponent
  ]
})
export class VoteModalComponent implements OnInit {
  voteDetail: VoteDetail | null = null;
  selectedAnswers: Map<number, number> = new Map(); // Map<questionId, optionId>
  loading = false;
  error = '';

  constructor(
    public dialogRef: MatDialogRef<VoteModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: VoteModalData,
    private meetingService: MeetingManagementService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit() {
    this.loadVoteDetail();
  }

  loadVoteDetail() {
    this.loading = true;
    
    this.meetingService.getVoteForSelection(this.data.meetingVoteId)
      .subscribe({
        next: (data) => {
          this.voteDetail = data;
          this.loading = false;
        },
        error: (error: any) => {
          this.error = 'Không thể tải thông tin biểu quyết';
          this.loading = false;
        }
      });
  }

  onOptionSelect(questionId: number, optionId: number) {
    this.selectedAnswers.set(questionId, optionId);
  }

  isOptionSelected(questionId: number, optionId: number): boolean {
    return this.selectedAnswers.get(questionId) === optionId;
  }

  isAllQuestionsAnswered(): boolean {
    if (!this.voteDetail) return false;
    return this.voteDetail.questions.every(q => this.selectedAnswers.has(q.id));
  }

  onSubmit() {
    if (!this.voteDetail) return;

    const confirmDialog = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Xác nhận gửi phiếu bầu',
        message: 'Bạn có chắc chắn muốn gửi phiếu bầu này? Sau khi gửi, bạn không thể thay đổi lựa chọn của mình.'
      },
      width: '400px'
    });

    confirmDialog.afterClosed().subscribe(result => {
      if (result) {
        const questionSelections = Array.from(this.selectedAnswers.entries()).map(([questionId, optionId]) => ({
          questionId,
          optionId
        }));

        const voteData: SubmitVoteRequest = {
          meetingId: this.data.meetingId,
          meetingVoteId: this.data.meetingVoteId,
          questionSelections
        };

        this.loading = true;
        this.meetingService.submitVote(voteData).subscribe({
          next: () => {
            this.snackBar.open('Gửi phiếu bầu thành công', 'Đóng', {
              duration: 3000,
              horizontalPosition: 'center',
              verticalPosition: 'top'
            });
            this.dialogRef.close(true);
          },
          error: (error: any) => {
            this.error = 'Không thể gửi phiếu bầu';
            this.snackBar.open('Gửi phiếu bầu thất bại', 'Đóng', {
              duration: 3000,
              horizontalPosition: 'center',
              verticalPosition: 'top'
            });
            this.loading = false;
          }
        });
      }
    });
  }

  onCancel() {
    const confirmDialog = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Xác nhận hủy',
        message: 'Bạn có chắc chắn muốn hủy bỏ phiếu? Tất cả các lựa chọn của bạn sẽ bị mất.'
      },
      width: '400px'
    });

    confirmDialog.afterClosed().subscribe(result => {
      if (result) {
        this.dialogRef.close();
      }
    });
  }
}
