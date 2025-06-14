import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageSectionComponent } from './manage-section.component';

describe('ManageSectionComponent', () => {
  let component: ManageSectionComponent;
  let fixture: ComponentFixture<ManageSectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManageSectionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManageSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
