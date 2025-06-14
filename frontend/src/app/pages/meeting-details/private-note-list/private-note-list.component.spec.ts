import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrivateNoteListComponent } from './private-note-list.component';

describe('PrivateNoteListComponent', () => {
  let component: PrivateNoteListComponent;
  let fixture: ComponentFixture<PrivateNoteListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PrivateNoteListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PrivateNoteListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
