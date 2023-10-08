import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlansContainerComponent } from './plans-container.component';

describe('PlansContainerComponent', () => {
  let component: PlansContainerComponent;
  let fixture: ComponentFixture<PlansContainerComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PlansContainerComponent]
    });
    fixture = TestBed.createComponent(PlansContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
