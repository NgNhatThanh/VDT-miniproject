import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MeetingManagementService, Note } from '../../../../services/meeting-management.service';
import { ConfirmDialogComponent } from '../../../../common/components/confirm-dialog/confirm-dialog.component';

interface DialogData {
  meetingId: number;
}

@Component({
  selector: 'app-private-note-modal',
  templateUrl: './private-note-modal.component.html',
  styles: [],
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatSnackBarModule
  ]
})
export class PrivateNoteModalComponent implements OnInit {
  noteContent: string = '';
  notes: Note[] = [];
  showAddNoteForm: boolean = false;
  editingNote: Note | null = null;

  constructor(
    public dialogRef: MatDialogRef<PrivateNoteModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData,
    private meetingService: MeetingManagementService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {}

  ngOnInit() {
    this.loadNotes();
  }

  private loadNotes() {
    this.meetingService.getListNote(this.data.meetingId).subscribe({
      next: (notes) => {
        this.notes = notes;
      },
      error: (error) => {
        console.error('Error loading notes:', error);
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
        this.meetingService.deleteNote(note.id).subscribe({
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

  onCancel(): void {
    this.dialogRef.close();
  }

  onSave(): void {
    if (!this.noteContent.trim()) {
      this.showSnackBar('Vui lòng nhập nội dung ghi chú', 'error');
      return;
    }

    if (this.editingNote) {
      // Update existing note
      this.meetingService.updateNote(this.editingNote.id, this.noteContent).subscribe({
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
      this.meetingService.addNote(this.data.meetingId, this.noteContent).subscribe({
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
