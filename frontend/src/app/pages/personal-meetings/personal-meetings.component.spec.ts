import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PersonalMeetingsComponent } from './personal-meetings.component';

describe('PersonalMeetingsComponent', () => {
  let component: PersonalMeetingsComponent;
  let fixture: ComponentFixture<PersonalMeetingsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PersonalMeetingsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PersonalMeetingsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
