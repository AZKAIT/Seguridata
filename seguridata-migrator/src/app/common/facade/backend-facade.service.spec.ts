import { TestBed } from '@angular/core/testing';

import { BackendFacadeService } from './backend-facade.service';

describe('BackendFacadeService', () => {
  let service: BackendFacadeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BackendFacadeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
