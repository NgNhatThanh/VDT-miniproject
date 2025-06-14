import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HistorySectionComponent } from './history-section.component';

describe('HistorySectionComponent', () => {
  let component: HistorySectionComponent;
  let fixture: ComponentFixture<HistorySectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HistorySectionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HistorySectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
