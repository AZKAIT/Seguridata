import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WizardStepContainerComponent } from './wizard-step-container.component';

describe('WizardStepContainerComponent', () => {
  let component: WizardStepContainerComponent;
  let fixture: ComponentFixture<WizardStepContainerComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [WizardStepContainerComponent]
    });
    fixture = TestBed.createComponent(WizardStepContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
