import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VoteModalComponent } from './vote-modal.component';

describe('VoteModalComponent', () => {
  let component: VoteModalComponent;
  let fixture: ComponentFixture<VoteModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VoteModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VoteModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
