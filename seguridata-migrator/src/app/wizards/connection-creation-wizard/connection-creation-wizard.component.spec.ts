import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConnectionCreationWizardComponent } from './connection-creation-wizard.component';

describe('ConnectionCreationWizardComponent', () => {
  let component: ConnectionCreationWizardComponent;
  let fixture: ComponentFixture<ConnectionCreationWizardComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ConnectionCreationWizardComponent]
    });
    fixture = TestBed.createComponent(ConnectionCreationWizardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
