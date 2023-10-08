import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConnectionDataFormComponent } from './connection-data-form.component';

describe('ConnectionDataFormComponent', () => {
  let component: ConnectionDataFormComponent;
  let fixture: ComponentFixture<ConnectionDataFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ConnectionDataFormComponent]
    });
    fixture = TestBed.createComponent(ConnectionDataFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
