import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExaminerPageComponent } from './examiner-page.component';

describe('ExaminerPageComponent', () => {
  let component: ExaminerPageComponent;
  let fixture: ComponentFixture<ExaminerPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExaminerPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExaminerPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
