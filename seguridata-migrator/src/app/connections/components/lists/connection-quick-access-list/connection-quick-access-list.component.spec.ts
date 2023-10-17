import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConnectionQuickAccessListComponent } from './connection-quick-access-list.component';

describe('ConnectionQuickAccessListComponent', () => {
  let component: ConnectionQuickAccessListComponent;
  let fixture: ComponentFixture<ConnectionQuickAccessListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ConnectionQuickAccessListComponent]
    });
    fixture = TestBed.createComponent(ConnectionQuickAccessListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
