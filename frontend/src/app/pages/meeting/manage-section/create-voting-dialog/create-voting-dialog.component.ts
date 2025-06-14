import { Component, OnInit, ViewChild, ElementRef, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormArray, FormControl } from '@angular/forms';
import { MatDialogRef, MatDialogModule, MatDialog, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ConfirmDialogComponent } from '../../../../common/components/confirm-dialog/confirm-dialog.component';
import { CreateVotingRequest, MeetingManagementService } from '../../../../services/meeting-management.service';
import { catchError, finalize } from 'rxjs/operators';
import { of } from 'rxjs';
import { DocumentCardComponent, DocumentInfo } from '../../../../components/document-card/document-card.component';
import { MeetingService } from '../../../../services/meeting.service';

@Component({
  selector: 'app-create-voting-dialog',
  templateUrl: './create-voting-dialog.component.html',
  standalone: true,
  imports: [
    CommonModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatIconModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    DocumentCardComponent
  ],
})
export class CreateVotingDialogComponent implements OnInit {
  @ViewChild('fileInput') fileInput!: ElementRef;
  votingForm: FormGroup;
  selectedFiles: File[] = [];
  uploadedDocuments: DocumentInfo[] = [];
  isSubmitting = false;
  isUploading = false;
  meetingId: number;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<CreateVotingDialogComponent>,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private meetingService: MeetingService,
    private meetingManagementService: MeetingManagementService,
    @Inject(MAT_DIALOG_DATA) public data: { meetingId: number }
  ) {
    this.meetingId = data.meetingId;
    this.votingForm = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      startTime: [null, Validators.required],
      endTime: [null, Validators.required],
      votingType: ['PUBLIC', Validators.required],
      questions: this.fb.array([])
    });
  }

  ngOnInit(): void {
    // Không cần lấy meetingId từ route nữa
  }

  // Getter cho FormArray questions
  get questions() {
    return this.votingForm.get('questions') as FormArray;
  }

  // Lấy FormArray options của một câu hỏi
  getQuestionOptions(questionIndex: number) {
    return this.questions.at(questionIndex).get('options') as FormArray;
  }

  // Thêm câu hỏi mới
  addQuestion() {
    const questionForm = this.fb.group({
      title: ['', Validators.required],
      options: this.fb.array([])
    });
    this.questions.push(questionForm);
  }

  // Xóa câu hỏi
  removeQuestion(index: number) {
    this.questions.removeAt(index);
  }

  // Thêm lựa chọn mới cho câu hỏi
  addOption(questionIndex: number) {
    const options = this.getQuestionOptions(questionIndex);
    const optionForm = this.fb.group({
      content: ['', Validators.required]
    });
    options.push(optionForm);
  }

  // Xóa lựa chọn
  removeOption(questionIndex: number, optionIndex: number) {
    const options = this.getQuestionOptions(questionIndex);
    options.removeAt(optionIndex);
  }

  // Xử lý khi chọn file
  onFileSelected(event: any) {
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
      next: (response) => {
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
      error: (error) => {
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

  // Hiển thị dialog xác nhận
  showConfirmDialog(title: string, message: string): Promise<boolean> {
    return new Promise((resolve) => {
      const dialogRef = this.dialog.open(ConfirmDialogComponent, {
        width: '400px',
        data: { title, message }
      });

      dialogRef.afterClosed().subscribe(result => {
        resolve(result === true);
      });
    });
  }

  // Xử lý khi click nút hủy
  async onCancel(): Promise<void> {
    const confirmed = await this.showConfirmDialog(
      'Xác nhận hủy',
      'Bạn có chắc chắn muốn hủy tạo biểu quyết này?'
    );
    if (confirmed) {
      this.dialogRef.close();
    }
  }

  // Kiểm tra validation cho câu hỏi và lựa chọn
  validateQuestions(): boolean {
    const questions = this.questions.controls;
    
    if (questions.length === 0) {
      this.snackBar.open('Vui lòng thêm ít nhất một câu hỏi', 'Đóng', {
        duration: 3000,
        horizontalPosition: 'center',
        verticalPosition: 'top',
        panelClass: ['error-snackbar']
      });
      return false;
    }

    // Kiểm tra câu hỏi trùng nhau
    if (this.checkDuplicateQuestions()) {
      this.snackBar.open('Có các câu hỏi trùng nhau', 'Đóng', {
        duration: 3000,
        horizontalPosition: 'center',
        verticalPosition: 'top',
        panelClass: ['error-snackbar']
      });
      return false;
    }

    for (let i = 0; i < questions.length; i++) {
      const options = this.getQuestionOptions(i).controls;
      if (options.length < 2) {
        this.snackBar.open(`Câu hỏi ${i + 1} cần ít nhất 2 lựa chọn`, 'Đóng', {
          duration: 3000,
          horizontalPosition: 'center',
          verticalPosition: 'top',
          panelClass: ['error-snackbar']
        });
        return false;
      }

      // Kiểm tra các lựa chọn trùng nhau
      if (this.checkDuplicateOptions(i)) {
        this.snackBar.open(`Câu hỏi ${i + 1} có các lựa chọn trùng nhau`, 'Đóng', {
          duration: 3000,
          horizontalPosition: 'center',
          verticalPosition: 'top',
          panelClass: ['error-snackbar']
        });
        return false;
      }
    }

    return true;
  }

  // Kiểm tra các câu hỏi trùng nhau
  checkDuplicateQuestions(): boolean {
    const questions = this.questions.controls;
    const questionTitles = questions.map(question => question.get('title')?.value?.trim().toLowerCase());
    const uniqueQuestions = new Set(questionTitles);
    return uniqueQuestions.size !== questionTitles.length;
  }

  // Kiểm tra các lựa chọn trùng nhau trong một câu hỏi
  checkDuplicateOptions(questionIndex: number): boolean {
    const options = this.getQuestionOptions(questionIndex).controls;
    const optionContents = options.map(option => option.get('content')?.value?.trim().toLowerCase());
    const uniqueOptions = new Set(optionContents);
    return uniqueOptions.size !== optionContents.length;
  }

  // Xử lý khi submit form
  async onSubmit(): Promise<void> {
    if (this.votingForm.valid) {
      // Kiểm tra validation cho câu hỏi và lựa chọn
      if (!this.validateQuestions()) {
        return;
      }

      const confirmed = await this.showConfirmDialog(
        'Xác nhận tạo biểu quyết',
        'Bạn có chắc chắn muốn tạo biểu quyết này?'
      );
      if (confirmed) {
        this.isSubmitting = true;
        const formValue = this.votingForm.value;
        const votingData: CreateVotingRequest = {
          meetingId: this.meetingId,
          title: formValue.title,
          description: formValue.description,
          startTime: formValue.startTime,
          endTime: formValue.endTime,
          type: formValue.votingType,
          documentIds: this.uploadedDocuments.map(doc => doc.id), // Thêm documentIds từ uploadedDocuments
          questions: formValue.questions.map((q: any) => ({
            title: q.title,
            options: q.options.map((o: any) => ({
              content: o.content
            }))
          }))
        };

        this.meetingManagementService.createVoting(votingData)
          .pipe(
            catchError(error => {
              this.snackBar.open(
                error.error?.message || 'Có lỗi xảy ra khi tạo biểu quyết',
                'Đóng',
                {
                  duration: 3000,
                  horizontalPosition: 'center',
                  verticalPosition: 'top',
                  panelClass: ['error-snackbar']
                }
              );
              return of(null);
            }),
            finalize(() => {
              this.isSubmitting = false;
            })
          )
          .subscribe(response => {
            if (response) {
              this.snackBar.open('Tạo biểu quyết thành công', 'Đóng', {
                duration: 3000,
                horizontalPosition: 'center',
                verticalPosition: 'top',
                panelClass: ['success-snackbar']
              });
              this.dialogRef.close(response);
            }
          });
      }
    }
  }
}
