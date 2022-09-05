import { TestBed } from '@angular/core/testing';

import { UserserService } from './userser.service';

describe('UserserService', () => {
  let service: UserserService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UserserService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
