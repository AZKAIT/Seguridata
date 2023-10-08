import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConnectionsContainerComponent } from './connections-container.component';

describe('ConnectionsContainerComponent', () => {
  let component: ConnectionsContainerComponent;
  let fixture: ComponentFixture<ConnectionsContainerComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ConnectionsContainerComponent]
    });
    fixture = TestBed.createComponent(ConnectionsContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
