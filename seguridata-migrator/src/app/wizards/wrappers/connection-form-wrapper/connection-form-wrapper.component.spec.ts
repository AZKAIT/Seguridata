import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConnectionFormWrapperComponent } from './connection-form-wrapper.component';

describe('ConnectionFormWrapperComponent', () => {
  let component: ConnectionFormWrapperComponent;
  let fixture: ComponentFixture<ConnectionFormWrapperComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ConnectionFormWrapperComponent]
    });
    fixture = TestBed.createComponent(ConnectionFormWrapperComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
