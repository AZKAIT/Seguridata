import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectFormWrapperComponent } from './project-form-wrapper.component';

describe('ProjectFormWrapperComponent', () => {
  let component: ProjectFormWrapperComponent;
  let fixture: ComponentFixture<ProjectFormWrapperComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProjectFormWrapperComponent]
    });
    fixture = TestBed.createComponent(ProjectFormWrapperComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
