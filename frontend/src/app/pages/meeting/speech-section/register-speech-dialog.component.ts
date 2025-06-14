import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { ConfirmDialogComponent } from '../../../common/components/confirm-dialog/confirm-dialog.component';
import { MeetingManagementService } from '../../../services/meeting-management.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-register-speech-dialog',
  template: `
    <div class="p-6">
      <h2 mat-dialog-title class="text-xl font-semibold mb-4">Đăng ký phát biểu</h2>
      <mat-dialog-content>
        <form class="flex flex-col gap-4" #speechForm="ngForm">
          <mat-form-field>
            <mat-label>Nội dung phát biểu</mat-label>
            <textarea matInput [(ngModel)]="data.content" name="content" rows="4" 
                      placeholder="Nhập nội dung phát biểu của bạn"
                      required></textarea>
            <mat-error *ngIf="speechForm.form.get('content')?.hasError('required')">
              Vui lòng nhập nội dung phát biểu
            </mat-error>
          </mat-form-field>
          
          <mat-form-field>
            <mat-label>Thời lượng (phút)</mat-label>
            <input matInput type="number" [(ngModel)]="data.duration" name="duration" 
                   min="1" max="30" required>
            <mat-error *ngIf="speechForm.form.get('duration')?.hasError('required')">
              Vui lòng nhập thời lượng phát biểu
            </mat-error>
            <mat-error *ngIf="speechForm.form.get('duration')?.hasError('min') || speechForm.form.get('duration')?.hasError('max')">
              Thời lượng phải từ 1 đến 30 phút
            </mat-error>
          </mat-form-field>
        </form>
      </mat-dialog-content>
      <mat-dialog-actions align="end" class="mt-4">
        <button mat-button (click)="onCancel()">Hủy</button>
        <button mat-raised-button color="primary" (click)="onSubmit(speechForm)">Đăng ký</button>
      </mat-dialog-actions>
    </div>
  `,
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    FormsModule
  ]
})
export class RegisterSpeechDialog {
  constructor(
    public dialogRef: MatDialogRef<RegisterSpeechDialog>,
    @Inject(MAT_DIALOG_DATA) public data: { meetingId: number; content: string; duration: number },
    private dialog: MatDialog,
    private meetingService: MeetingManagementService,
    private snackBar: MatSnackBar
  ) {
    // Disable click outside to close
    dialogRef.disableClose = true;
  }

  onCancel(): void {
    const confirmDialog = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      disableClose: true,
      data: {
        title: 'Xác nhận hủy',
        message: 'Bạn có chắc chắn muốn hủy đăng ký phát biểu?'
      }
    });

    confirmDialog.afterClosed().subscribe(result => {
      if (result) {
        this.dialogRef.close();
      }
    });
  }

  onSubmit(form: any): void {
    if (form.valid) {
      const confirmDialog = this.dialog.open(ConfirmDialogComponent, {
        width: '400px',
        disableClose: true,
        data: {
          title: 'Xác nhận đăng ký',
          message: 'Bạn có chắc chắn muốn đăng ký phát biểu?'
        }
      });

      confirmDialog.afterClosed().subscribe(result => {
        if (result) {
          this.meetingService.registerSpeech({
            meetingId: this.data.meetingId,
            content: this.data.content,
            duration: this.data.duration
          }).subscribe({
            next: () => {
              this.snackBar.open('Đăng ký phát biểu thành công', 'Đóng', {
                duration: 3000,
                horizontalPosition: 'end',
                verticalPosition: 'top'
              });
              this.dialogRef.close(true);
            },
            error: (error) => {
              this.snackBar.open('Đăng ký phát biểu thất bại: ' + error.message, 'Đóng', {
                duration: 5000,
                horizontalPosition: 'end',
                verticalPosition: 'top'
              });
            }
          });
        }
      });
    }
  }
} 