import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AgentpageComponent } from './agentpage.component';

describe('AgentpageComponent', () => {
  let component: AgentpageComponent;
  let fixture: ComponentFixture<AgentpageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AgentpageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AgentpageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
