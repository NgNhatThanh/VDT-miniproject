import { Component, Inject, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { OnlineUser } from '../../../../services/meeting-management.service';
import { MeetingManagementService } from '../../../../services/meeting-management.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-participants-modal',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule, MatChipsModule],
  template: `
    <div class="p-4">
      <h2 class="text-lg font-semibold mb-3">Danh sách người tham gia</h2>
      <div class="h-[400px] border border-gray-200 rounded-lg">
        <div class="h-full overflow-y-auto [&::-webkit-scrollbar]:w-1
          [&::-webkit-scrollbar-track]:rounded-full
          [&::-webkit-scrollbar-track]:bg-gray-100
          [&::-webkit-scrollbar-thumb]:rounded-full
          [&::-webkit-scrollbar-thumb]:bg-gray-400">
          <div class="space-y-2 p-2">
            <div *ngFor="let participant of participants" 
                 class="flex items-center gap-3 p-2 rounded-lg transition-colors duration-200 hover:bg-gray-100 cursor-pointer">
              <img [src]="participant.picture" [alt]="participant.fullName" 
                   class="w-10 h-10 rounded-full object-cover">
              <div class="flex-1 min-w-0">
                <h3 class="text-base font-medium truncate">{{ participant.fullName }}</h3>
                <div class="flex flex-wrap gap-1 mt-1">
                  <mat-chip *ngFor="let role of participant.join.roles" 
                  style="border-radius: 5px;"
                           color="primary" selected
                           class="text-xs">
                    {{ role.name }}
                  </mat-chip>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="flex justify-end mt-4">
        <button mat-button (click)="dialogRef.close()" class="text-gray-600">Đóng</button>
      </div>
    </div>
  `
})
export class ParticipantsModalComponent implements OnInit, OnDestroy {
  participants: OnlineUser[] = [];
  private subscription = new Subscription();

  constructor(
    public dialogRef: MatDialogRef<ParticipantsModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { participants: OnlineUser[] },
    private meetingService: MeetingManagementService
  ) {
    this.participants = data.participants;
  }

  ngOnInit() {
    // Lắng nghe sự thay đổi của meeting info từ service
    this.subscription.add(
      this.meetingService.meetingInfo$.subscribe(info => {
        if (info?.onlineUsers) {
          this.participants = info.onlineUsers;
        }
      })
    );
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }
} 