import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectCreationWizardComponent } from './project-creation-wizard.component';

describe('ProjectCreationWizardComponent', () => {
  let component: ProjectCreationWizardComponent;
  let fixture: ComponentFixture<ProjectCreationWizardComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProjectCreationWizardComponent]
    });
    fixture = TestBed.createComponent(ProjectCreationWizardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
