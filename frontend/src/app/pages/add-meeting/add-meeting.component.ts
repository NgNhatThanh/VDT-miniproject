import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MeetingService, MeetingLocation } from '../../services/meeting/meeting.service';
import { UserService } from '../../services/user/user.service';
import { MeetingRoleService } from '../../services/meeting-role/meeting-role.service';
import { User } from '../../models/user.model';
import { MeetingRole } from '../../models/meeting-role.model';
import { MatDialog } from '@angular/material/dialog';
import { UserPickerDialogComponent } from './user-picker-dialog.component';
import { of, tap, catchError } from 'rxjs';

@Component({
  selector: 'app-add-meeting',
  templateUrl: './add-meeting.component.html',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatButtonModule,
    MatIconModule,
    MatListModule,
    MatProgressSpinnerModule
  ]
})
export class AddMeetingComponent implements OnInit {
  meetingForm: FormGroup;
  users: User[] = [];
  roles: MeetingRole[] = [];
  locations: MeetingLocation[] = [];
  selectedFiles: File[] = [];
  roleUsers: { [roleId: number]: User[] } = {};
  loading: boolean = true;
  loadingError: boolean = false;
  isSubmitting: boolean = false;

  constructor(
    private fb: FormBuilder,
    private meetingService: MeetingService,
    private userService: UserService,
    private meetingRoleService: MeetingRoleService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {
    this.meetingForm = this.fb.group({
      title: ['', Validators.required],
      description: [''],
      location: ['', Validators.required],
      startTime: ['', Validators.required],
      endTime: ['', Validators.required],
      participants: [[]],
      roles: [[]]
    });
  }

  ngOnInit(): void {
    this.loading = true;
    this.loadingError = false;
    
    // Sử dụng Promise.all để đợi tất cả các request hoàn thành
    Promise.all([
      new Promise<void>((resolve) => this.loadLocations().subscribe({
        next: () => resolve(),
        error: () => resolve()
      })),
      new Promise<void>((resolve) => this.loadUsers().subscribe({
        next: () => resolve(),
        error: () => resolve()
      })),
      new Promise<void>((resolve) => this.loadRoles().subscribe({
        next: () => resolve(),
        error: () => resolve()
      }))
    ]).then(() => {
      this.loading = false;
    }).catch(() => {
      this.loading = false;
      this.loadingError = true;
    });
  }

  private loadLocations() {
    return this.meetingService.getLocations().pipe(
      tap(locations => {
        this.locations = locations;
      }),
      catchError(error => {
        console.error('Error loading locations:', error);
        return of([]);
      })
    );
  }

  loadUsers() {
    return this.userService.getAllUsers().pipe(
      tap(users => {
        this.users = users;
      }),
      catchError(error => {
        console.error('Error loading users:', error);
        return of([]);
      })
    );
  }

  loadRoles() {
    return this.meetingRoleService.getAllRoles().pipe(
      tap(roles => {
        this.roles = roles;
      }),
      catchError(error => {
        console.error('Error loading roles:', error);
        return of([]);
      })
    );
  }

  onFileSelected(event: any): void {
    const files = event.target.files;
    if (files) {
      this.selectedFiles = [...this.selectedFiles, ...files];
    }
  }

  removeFile(file: File): void {
    this.selectedFiles = this.selectedFiles.filter(f => f !== file);
  }

  onSubmit(): void {
    if (this.meetingForm.valid) {
      this.isSubmitting = true;
      const formValue = this.meetingForm.value;
      
      // Tạo mảng joins từ roleUsers
      const joins = Object.entries(this.roleUsers).flatMap(([roleId, users]) => 
        users.map(user => ({
          userId: user.id,
          roleId: parseInt(roleId)
        }))
      );

      const meetingData = {
        title: formValue.title,
        description: formValue.description,
        startTime: formValue.startTime,
        endTime: formValue.endTime,
        locationId: formValue.location,
        joins: joins,
        documentIds: [] // Tạm thời để trống
      };

      this.meetingService.addMeeting(meetingData).subscribe({
        next: (response) => {
          console.log('Meeting created successfully:', response);
          this.isSubmitting = false;
          this.snackBar.open('Tạo cuộc họp thành công!', 'Đóng', {
            duration: 3000,
            horizontalPosition: 'end',
            verticalPosition: 'top',
            panelClass: ['success-snackbar']
          });
          // Reset form và dữ liệu
          this.meetingForm.reset();
          this.selectedFiles = [];
          this.roleUsers = {};
        },
        error: (error) => {
          console.error('Error creating meeting:', error);
          this.isSubmitting = false;
          this.snackBar.open('Có lỗi xảy ra khi tạo cuộc họp. Vui lòng thử lại!', 'Đóng', {
            duration: 5000,
            horizontalPosition: 'end',
            verticalPosition: 'top',
            panelClass: ['error-snackbar']
          });
        }
      });
    }
  }

  onCancel(): void {
    this.meetingForm.reset();
    this.selectedFiles = [];
  }

  openUserPicker(role: MeetingRole) {
    const excluded = this.roleUsers[role.id] || [];
    const dialogRef = this.dialog.open(UserPickerDialogComponent, {
      data: { users: this.users, excluded, multi: true },
    });
    dialogRef.afterClosed().subscribe((users: User[]) => {
      if (Array.isArray(users) && users.length) {
        const current = this.roleUsers[role.id] || [];
        const merged = [...current, ...users.filter(u => !current.some(c => c.username === u.username))];
        this.roleUsers[role.id] = merged;
      }
    });
  }

  removeUserFromRole(role: MeetingRole, user: User) {
    if (!this.roleUsers[role.id]) return;
    this.roleUsers[role.id] = this.roleUsers[role.id].filter(u => u.username !== user.username);
  }
}
