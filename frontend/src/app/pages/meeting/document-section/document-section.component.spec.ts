import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DocumentSectionComponent } from './document-section.component';

describe('DocumentSectionComponent', () => {
  let component: DocumentSectionComponent;
  let fixture: ComponentFixture<DocumentSectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DocumentSectionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DocumentSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
