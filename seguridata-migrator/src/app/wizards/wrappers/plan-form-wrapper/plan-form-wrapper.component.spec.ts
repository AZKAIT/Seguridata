import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlanFormWrapperComponent } from './plan-form-wrapper.component';

describe('PlanFormWrapperComponent', () => {
  let component: PlanFormWrapperComponent;
  let fixture: ComponentFixture<PlanFormWrapperComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PlanFormWrapperComponent]
    });
    fixture = TestBed.createComponent(PlanFormWrapperComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
