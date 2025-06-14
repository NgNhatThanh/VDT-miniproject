import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrivateNoteModalComponent } from './private-note-modal.component';

describe('PrivateNoteModalComponent', () => {
  let component: PrivateNoteModalComponent;
  let fixture: ComponentFixture<PrivateNoteModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PrivateNoteModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PrivateNoteModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
