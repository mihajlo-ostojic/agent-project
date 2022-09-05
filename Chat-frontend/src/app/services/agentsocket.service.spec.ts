import { TestBed } from '@angular/core/testing';

import { AgentsocketService } from './agentsocket.service';

describe('AgentsocketService', () => {
  let service: AgentsocketService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AgentsocketService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
