import { TestBed } from '@angular/core/testing';

import { ExaminerService } from './examiner.service';

describe('ExaminerService', () => {
  let service: ExaminerService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ExaminerService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
