import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-leave-meeting-modal',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule],
  template: `
    <div class="p-6">
      <h2 class="text-xl font-semibold mb-4">Xác nhận rời cuộc họp</h2>
      <p class="text-gray-600 mb-6">Bạn có chắc chắn muốn rời khỏi cuộc họp này?</p>
      <div class="flex justify-end gap-3">
        <button mat-button (click)="dialogRef.close()" class="text-gray-600">Hủy</button>
        <button mat-raised-button color="warn" (click)="dialogRef.close('leave')">Rời cuộc họp</button>
      </div>
    </div>
  `
})
export class LeaveMeetingModalComponent {
  constructor(public dialogRef: MatDialogRef<LeaveMeetingModalComponent>) {}
} 