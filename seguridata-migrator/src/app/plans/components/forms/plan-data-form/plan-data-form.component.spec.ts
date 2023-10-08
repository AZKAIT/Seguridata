import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlanDataFormComponent } from './plan-data-form.component';

describe('PlanDataFormComponent', () => {
  let component: PlanDataFormComponent;
  let fixture: ComponentFixture<PlanDataFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PlanDataFormComponent]
    });
    fixture = TestBed.createComponent(PlanDataFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
