import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateVotingDialogComponent } from './create-voting-dialog.component';

describe('CreateVotingDialogComponent', () => {
  let component: CreateVotingDialogComponent;
  let fixture: ComponentFixture<CreateVotingDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateVotingDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateVotingDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
