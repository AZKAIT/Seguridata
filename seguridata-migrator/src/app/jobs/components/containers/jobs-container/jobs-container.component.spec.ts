import { ComponentFixture, TestBed } from '@angular/core/testing';

import { JobsContainerComponent } from './jobs-container.component';

describe('JobsContainerComponent', () => {
  let component: JobsContainerComponent;
  let fixture: ComponentFixture<JobsContainerComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [JobsContainerComponent]
    });
    fixture = TestBed.createComponent(JobsContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
