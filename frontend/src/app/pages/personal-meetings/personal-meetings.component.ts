import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatTabsModule } from '@angular/material/tabs';
import { MatDividerModule } from '@angular/material/divider';
import { MeetingService } from '../../services/meeting.service';
import { RouterModule } from '@angular/router';

interface Meeting {
  id: string;
  title: string;
  description: string;
  startTime: Date;
  endTime: Date;
  location: string;
  status: 'PENDING' | 'ACCEPTED' | 'REJECTED' | 'DELEGATED';
}

interface MeetingStatus {
  value: Meeting['status'];
  label: string;
  displayText: string;
  cssClass: string;
}

@Component({
  selector: 'app-personal-meetings',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatIconModule, MatCardModule, MatTabsModule, MatDividerModule, RouterModule],
  templateUrl: './personal-meetings.component.html',
})
export class PersonalMeetingsComponent implements OnInit {
  currentDate: Date = new Date();
  weekStart: Date = new Date();
  weekEnd: Date = new Date();
  weekDays: Date[] = [];
  meetings: Meeting[] = [];

  meetingStatuses: MeetingStatus[] = [
    {
      value: 'PENDING',
      label: 'Chưa xác nhận',
      displayText: 'Chưa xác nhận',
      cssClass: 'bg-yellow-100 text-orange-700'
    },
    {
      value: 'ACCEPTED',
      label: 'Xác nhận tham gia',
      displayText: 'Xác nhận tham gia',
      cssClass: 'bg-green-100 text-green-700'
    },
    {
      value: 'REJECTED',
      label: 'Không tham gia',
      displayText: 'Không tham gia',
      cssClass: 'bg-red-100 text-red-700'
    },
    {
      value: 'DELEGATED',
      label: 'Đã ủy quyền',
      displayText: 'Đã ủy quyền',
      cssClass: 'bg-blue-100 text-blue-700'
    }
  ];

  constructor(private meetingService: MeetingService) {}

  ngOnInit() {
    this.updateWeekRange();
    this.loadMeetings();
  }

  updateWeekRange() {
    const today = new Date(this.currentDate);
    const dayOfWeek = today.getDay();
    const diff = today.getDate() - dayOfWeek + (dayOfWeek === 0 ? -6 : 1);
    
    this.weekStart = new Date(today.setDate(diff));
    this.weekEnd = new Date(today.setDate(diff + 6));
    
    this.weekDays = [];
    for (let i = 0; i < 7; i++) {
      const date = new Date(this.weekStart);
      date.setDate(date.getDate() + i);
      this.weekDays.push(date);
    }
  }

  loadMeetings() {
    this.meetingService.getMeetingsForCalendar(this.weekStart, this.weekEnd)
      .subscribe({
        next: (response) => {
          this.meetings = response.map(meeting => ({
            id: meeting.meetingId.toString(),
            title: meeting.title,
            description: meeting.description,
            startTime: new Date(meeting.startTime),
            endTime: new Date(meeting.endTime),
            location: meeting.location,
            status: meeting.status as Meeting['status']
          }));
        },
        error: (error) => {
          console.error('Error loading meetings:', error);
        }
      });
  }

  previousWeek() {
    this.currentDate.setDate(this.currentDate.getDate() - 7);
    this.updateWeekRange();
    this.loadMeetings();
  }

  nextWeek() {
    this.currentDate.setDate(this.currentDate.getDate() + 7);
    this.updateWeekRange();
    this.loadMeetings();
  }

  getMeetingsByStatusAndDay(status: Meeting['status'], date: Date): Meeting[] {
    return this.meetings.filter(meeting => {
      const meetingDate = new Date(meeting.startTime);
      return meeting.status === status &&
             meetingDate.getDate() === date.getDate() &&
             meetingDate.getMonth() === date.getMonth() &&
             meetingDate.getFullYear() === date.getFullYear();
    });
  }

  hasMeetingsByStatus(status: Meeting['status']): boolean {
    return this.meetings.some(meeting => meeting.status === status);
  }

  formatTime(date: Date): string {
    return date.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
  }

  formatDate(date: Date): string {
    return date.toLocaleDateString('vi-VN', { weekday: 'long', day: 'numeric', month: 'long' });
  }

  isOngoing(meeting: Meeting): boolean {
    const now = new Date();
    const start = new Date(meeting.startTime);
    const end = new Date(meeting.endTime);
    return now >= start && now <= end;
  }

  isUpcoming(meeting: Meeting): boolean {
    const now = new Date();
    const start = new Date(meeting.startTime);
    const thirtyMinutesBefore = new Date(start.getTime() - 30 * 60 * 1000);
    return now >= thirtyMinutesBefore && now < start;
  }

  isEnded(meeting: Meeting): boolean {
    const now = new Date();
    const end = new Date(meeting.endTime);
    return now > end;
  }

  getMeetingStatus(meeting: Meeting): { text: string, class: string } {
    if (this.isOngoing(meeting)) {
      return { text: 'Đang diễn ra', class: 'bg-blue-500 text-white' };
    } else if (this.isUpcoming(meeting)) {
      return { text: 'Sắp diễn ra', class: 'bg-green-500 text-white' };
    } else if (this.isEnded(meeting)) {
      return { text: 'Đã kết thúc', class: 'bg-gray-500 text-white' };
    }
    return { text: '', class: '' };
  }
}
