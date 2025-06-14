import { Component } from '@angular/core';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-reject-reason-dialog',
  standalone: true,
  imports: [CommonModule, 
    MatButtonModule, 
    FormsModule, 
    MatFormFieldModule,
    MatInputModule,
    MatDialogModule],
  template: `
    <h2 
        mat-dialog-title class="text-primary mb-2" 
        style="font-size: 20px; font-weight: 600;"
    >
        LÝ DO TỪ CHỐI
    </h2>
    <mat-dialog-content>
        <form (ngSubmit)="onConfirm()" #form="ngForm" autocomplete="off">
        <mat-form-field style="width: 100%;">
            <mat-label>Lý do </mat-label>
            <textarea matInput [(ngModel)]="reason" name="reason" required rows="4" cdkFocusInitial></textarea>
            <mat-error *ngIf="form.submitted && !reason.trim()">Vui lòng nhập lý do từ chối</mat-error>
        </mat-form-field>
        <div class="flex gap-3 mt-5 justify-end">
            <button mat-stroked-button type="button" (click)="onCancel()">HỦY BỎ</button>
            <button mat-flat-button color="primary" type="submit" [disabled]="!reason.trim()">XÁC NHẬN</button>
        </div>
        </form>
    </mat-dialog-content>
    
  `
})
export class RejectReasonDialogComponent {
  reason = '';
  constructor(private dialogRef: MatDialogRef<RejectReasonDialogComponent>) {}
  onCancel() { this.dialogRef.close(); }
  onConfirm() {
    if (!this.reason.trim()) return;
    this.dialogRef.close(this.reason);
  }
} 