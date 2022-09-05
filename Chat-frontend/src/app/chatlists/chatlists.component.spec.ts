import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChatlistsComponent } from './chatlists.component';

describe('ChatlistsComponent', () => {
  let component: ChatlistsComponent;
  let fixture: ComponentFixture<ChatlistsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChatlistsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChatlistsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
