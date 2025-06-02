import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { User } from '../../models/user.model';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatSelectionList, MatListOption } from '@angular/material/list';

@Component({
  selector: 'app-user-picker-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatListModule,
    MatButtonModule,
    MatSelectionList,
    MatListOption
  ],
  template: `
    <div style="min-width:350px;" class="p-4">
      <h2 class="text-xl font-semibold mb-4">CHỌN CÁ NHÂN</h2>  
      <mat-form-field class="w-full mb-4">
        <mat-label>Tìm kiếm</mat-label>
        <input matInput [formControl]="searchControl" placeholder="Nhập tên cá nhân để tìm kiếm">
      </mat-form-field>
      <mat-selection-list #userList [multiple]="data.multi" [(ngModel)]="selectedUsers" style="max-height: 300px; overflow-y: auto;">
        <mat-list-option *ngIf="data.multi" [value]="'ALL'" (click)="toggleSelectAll(userList)">
          <div class="flex items-center">
            <span class="font-medium">Tất cả</span>
          </div>
        </mat-list-option>
        <mat-list-option *ngFor="let user of filteredUsers" [value]="user">
          <div class="flex items-center">
            <img matListAvatar [src]="user.picture" alt="avatar" class="w-10 h-10 rounded-full" />
            <div class="flex flex-col ml-3">
              <span class="font-medium">{{ user.firstName }} {{ user.lastName }}</span>
              <span class="text-xs text-gray-500">{{ user.email }}</span>
            </div>
          </div>
        </mat-list-option>
      </mat-selection-list>
    </div>
    <div class="flex justify-end gap-2 mt-4 px-4 pb-2">
      <button mat-stroked-button (click)="onCancel()">HỦY BỎ</button>
      <button mat-flat-button color="primary" [disabled]="!selectedUsers.length" (click)="onConfirm()">XÁC NHẬN</button>
    </div>
  `,
  styles: [`
    .selected, .bg-blue-50 { background: #e3f2fd !important; }
    mat-dialog-content { min-width: 350px; }
    mat-form-field { width: 100%; }
    .cursor-pointer { cursor: pointer; }
    .w-10 { width: 40px; }
    .h-10 { height: 40px; }
    .ml-3 { margin-left: 0.75rem; }
    .font-medium { font-weight: 500; }
    .text-xs { font-size: 0.75rem; }
    .text-gray-500 { color: #6b7280; }
    .flex { display: flex; }
    .flex-col { flex-direction: column; }
    .items-center { align-items: center; }
    .w-full { width: 100%; }
    .py-2 { padding-top: 0.5rem; padding-bottom: 0.5rem; }
    .mb-4 { margin-bottom: 1rem; }
    .px-4 { padding-left: 1rem; padding-right: 1rem; }
    .pb-2 { padding-bottom: 0.5rem; }
  `]
})
export class UserPickerDialogComponent {
  searchControl = new FormControl('');
  filteredUsers: User[] = [];
  selectedUsers: User[] = [];

  constructor(
    public dialogRef: MatDialogRef<UserPickerDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { users: User[], excluded: User[], multi: boolean }
  ) {
    this.filteredUsers = this.data.users.filter(u => !this.data.excluded.some(e => e.username === u.username));
    this.searchControl.valueChanges.subscribe(val => {
      this.filteredUsers = this.data.users.filter(u =>
        !this.data.excluded.some(e => e.username === u.username) &&
        (`${u.firstName} ${u.lastName} ${u.email}`.toLowerCase().includes(val?.toLowerCase() || ''))
      );
    });
  }

  onConfirm() {
    if (!this.data.multi && this.selectedUsers.length > 0) {
      // Trong chế độ single selection, chỉ trả về user đầu tiên
      this.dialogRef.close(this.selectedUsers);
    } else {
      // Trong chế độ multiple selection, trả về tất cả users đã chọn
      const users = this.selectedUsers.filter(u => typeof u === 'object' && u && 'username' in u);
      this.dialogRef.close(users);
    }
  }

  onCancel() {
    this.dialogRef.close();
  }

  toggleSelectAll(userList: any) {
    if (this.isAllSelected(userList)) {
      userList.deselectAll();
      this.selectedUsers = [];
    } else {
      this.selectedUsers = [...this.filteredUsers];
      userList.selectAll();
    }
  }

  isAllSelected(userList: any): boolean {
    return this.selectedUsers.length === this.filteredUsers.length;
  }
} 