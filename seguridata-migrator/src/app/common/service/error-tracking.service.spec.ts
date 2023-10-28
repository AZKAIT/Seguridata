import { TestBed } from '@angular/core/testing';

import { ErrorTrackingService } from './error-tracking.service';

describe('ErrorTrackingService', () => {
  let service: ErrorTrackingService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ErrorTrackingService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
