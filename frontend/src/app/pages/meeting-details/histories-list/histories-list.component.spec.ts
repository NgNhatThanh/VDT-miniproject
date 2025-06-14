import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HistoriesListComponent } from './histories-list.component';

describe('HistoriesListComponent', () => {
  let component: HistoriesListComponent;
  let fixture: ComponentFixture<HistoriesListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HistoriesListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HistoriesListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
