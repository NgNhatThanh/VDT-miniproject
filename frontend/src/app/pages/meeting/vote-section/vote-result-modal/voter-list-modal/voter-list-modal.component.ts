import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { VoterInfo } from '../../../../../services/meeting-management.service';

@Component({
  selector: 'app-voter-list-modal',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule, MatIconModule],
  template: `
    <div class="p-6 h-[500px] flex flex-col">
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-xl font-bold">Danh sách người đã bỏ phiếu</h2>
        <button mat-icon-button (click)="dialogRef.close()">
          <mat-icon>close</mat-icon>
        </button>
      </div>

      <div class="flex-1 overflow-y-auto [&::-webkit-scrollbar]:w-1.5
        [&::-webkit-scrollbar-track]:bg-gray-100
        [&::-webkit-scrollbar-thumb]:bg-gray-300
        [&::-webkit-scrollbar-thumb]:rounded-full
        [&::-webkit-scrollbar-track]:rounded-full">
        <div class="space-y-3 pr-2">
          <div *ngFor="let voter of data.voters" class="flex items-center gap-3 p-2 hover:bg-gray-50 rounded-lg">
            <img [src]="voter.picture" 
                 [alt]="voter.fullName"
                 class="w-10 h-10 rounded-full object-cover">
            <span class="text-gray-700">{{ voter.fullName }}</span>
          </div>
        </div>
      </div>

      <div class="mt-6 flex justify-end">
        <button mat-button (click)="dialogRef.close()">Đóng</button>
      </div>
    </div>
  `
})
export class VoterListModalComponent {
  constructor(
    public dialogRef: MatDialogRef<VoterListModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { voters: VoterInfo[] }
  ) {}
} 