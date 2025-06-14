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
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MeetingService, MeetingLocation, DocumentResponse } from '../../services/meeting/meeting.service';
import { UserService } from '../../services/user/user.service';
import { MeetingRoleService } from '../../services/meeting-role/meeting-role.service';
import { User } from '../../models/user.model';
import { MeetingRole } from '../../models/meeting-role.model';
import { MatDialog } from '@angular/material/dialog';
import { UserPickerDialogComponent } from './user-picker-dialog.component';
import { of, tap, catchError } from 'rxjs';
import { DocumentCardComponent, DocumentInfo } from '../../components/document-card/document-card.component';

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
    MatProgressSpinnerModule,
    MatTooltipModule,
    DocumentCardComponent
  ]
})
export class AddMeetingComponent implements OnInit {
  meetingForm: FormGroup;
  users: User[] = [];
  roles: MeetingRole[] = [];
  locations: MeetingLocation[] = [];
  uploadedDocuments: DocumentInfo[] = [];
  roleUsers: { [roleId: number]: User[] } = {};
  loading: boolean = true;
  loadingError: boolean = false;
  isSubmitting: boolean = false;
  isUploading: boolean = false;

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
    const file = event.target.files[0];
    if (file) {
      // Kiểm tra dung lượng file (5MB = 5 * 1024 * 1024 bytes)
      const maxSize = 5 * 1024 * 1024;
      if (file.size > maxSize) {
        this.snackBar.open('Dung lượng file không được vượt quá 5MB!', 'Đóng', {
          duration: 3000,
          horizontalPosition: 'end',
          verticalPosition: 'top',
          panelClass: ['error-snackbar']
        });
        return;
      }

      this.uploadFile(file);
    }
  }

  uploadFile(file: File): void {
    this.isUploading = true;
    this.meetingService.uploadDocument(file).subscribe({
      next: (response: DocumentResponse) => {
        this.uploadedDocuments.push({
          id: response.id,
          name: response.name,
          size: response.size,
          url: response.url
        });
        this.snackBar.open(`Upload tài liệu ${file.name} thành công!`, 'Đóng', {
          duration: 3000,
          horizontalPosition: 'end',
          verticalPosition: 'top',
          panelClass: ['success-snackbar']
        });
        this.isUploading = false;
      },
      error: (error: any) => {
        console.error('Error uploading file:', error);
        this.snackBar.open(`Upload tài liệu ${file.name} thất bại!`, 'Đóng', {
          duration: 3000,
          horizontalPosition: 'end',
          verticalPosition: 'top',
          panelClass: ['error-snackbar']
        });
        this.isUploading = false;
      }
    });
  }

  removeUploadedDocument(doc: DocumentInfo): void {
    this.uploadedDocuments = this.uploadedDocuments.filter(d => d.id !== doc.id);
    this.snackBar.open(`Đã xóa tài liệu ${doc.name}`, 'Đóng', {
      duration: 2000,
      horizontalPosition: 'end',
      verticalPosition: 'top',
      panelClass: ['success-snackbar']
    });
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
        documentIds: this.uploadedDocuments.map(doc => doc.id) // Thêm documentIds từ uploadedDocuments
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
          this.uploadedDocuments = [];
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
    this.uploadedDocuments = [];
    this.roleUsers = {};
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
