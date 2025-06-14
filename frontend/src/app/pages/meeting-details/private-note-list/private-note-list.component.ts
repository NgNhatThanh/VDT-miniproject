import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MeetingService } from '../../../services/meeting.service';
import { CommonModule } from '@angular/common';
import { MeetingManagementService, Note } from '../../../services/meeting-management.service';
import { MatIconModule } from '@angular/material/icon';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ConfirmDialogComponent } from '../../../common/components/confirm-dialog/confirm-dialog.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-private-note-list',
  standalone: true,
  imports: [
    CommonModule, 
    MatIconModule,
    FormsModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSnackBarModule
  ],
  templateUrl: './private-note-list.component.html'
})
export class PrivateNoteListComponent implements OnInit {
  notes: Note[] = [];
  noteContent: string = '';
  showAddNoteForm: boolean = false;
  editingNote: Note | null = null;
  currentMeetingId: number | null = null;

  constructor(
    private meetingService: MeetingService,
    private router: Router,
    private meetingManagementService: MeetingManagementService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {}

  ngOnInit() {
    this.meetingService.currentMeeting$.subscribe(meeting => {
      if (!meeting) {
        this.router.navigate(['/lich-hop-ca-nhan']);
        return;
      }

      if (meeting.join.status !== 'ACCEPTED') {
        alert('Forbidden');
        window.history.back();
        return;
      }

      this.currentMeetingId = meeting.id;
      this.loadNotes();
    });
  }

  formatDateTime(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleString('vi-VN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  private loadNotes() {
    if (!this.currentMeetingId) return;
    
    this.meetingManagementService.getListNote(this.currentMeetingId).subscribe({
      next: (notes) => {
        this.notes = notes;
      },
      error: (error) => {
        console.error('Lỗi khi lấy danh sách ghi chú:', error);
        this.showSnackBar('Không thể tải danh sách ghi chú', 'error');
      }
    });
  }

  onEditNote(note: Note): void {
    this.editingNote = note;
    this.noteContent = note.content;
    this.showAddNoteForm = true;
  }

  onDeleteNote(note: Note): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Xác nhận xóa',
        message: 'Bạn có chắc chắn muốn xóa ghi chú này không?',
        confirmText: 'Xóa',
        cancelText: 'Hủy'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.meetingManagementService.deleteNote(note.id).subscribe({
          next: () => {
            this.notes = this.notes.filter(n => n.id !== note.id);
            this.showSnackBar('Xóa ghi chú thành công', 'success');
          },
          error: (error) => {
            console.error('Error deleting note:', error);
            this.showSnackBar('Không thể xóa ghi chú', 'error');
          }
        });
      }
    });
  }

  onSave(): void {
    if (!this.noteContent.trim()) {
      this.showSnackBar('Vui lòng nhập nội dung ghi chú', 'error');
      return;
    }

    if (!this.currentMeetingId) return;

    if (this.editingNote) {
      // Update existing note
      this.meetingManagementService.updateNote(this.editingNote.id, this.noteContent).subscribe({
        next: (updatedNote) => {
          const index = this.notes.findIndex(n => n.id === updatedNote.id);
          if (index !== -1) {
            this.notes[index] = updatedNote;
          }
          this.resetForm();
          this.showSnackBar('Cập nhật ghi chú thành công', 'success');
        },
        error: (error) => {
          console.error('Error updating note:', error);
          this.showSnackBar('Không thể cập nhật ghi chú', 'error');
        }
      });
    } else {
      // Add new note
      this.meetingManagementService.addNote(this.currentMeetingId, this.noteContent).subscribe({
        next: (newNote) => {
          this.notes.unshift(newNote);
          this.resetForm();
          this.showSnackBar('Thêm ghi chú thành công', 'success');
        },
        error: (error) => {
          console.error('Error adding note:', error);
          this.showSnackBar('Không thể thêm ghi chú', 'error');
        }
      });
    }
  }

  resetForm(): void {
    this.noteContent = '';
    this.showAddNoteForm = false;
    this.editingNote = null;
  }

  private showSnackBar(message: string, type: 'success' | 'error'): void {
    this.snackBar.open(message, 'Đóng', {
      duration: 3000,
      horizontalPosition: 'end',
      verticalPosition: 'top',
      panelClass: type === 'success' ? ['bg-green-500'] : ['bg-red-500']
    });
  }
}
