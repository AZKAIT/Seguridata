import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectDataFormComponent } from './project-data-form.component';

describe('ProjectDataFormComponent', () => {
  let component: ProjectDataFormComponent;
  let fixture: ComponentFixture<ProjectDataFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProjectDataFormComponent]
    });
    fixture = TestBed.createComponent(ProjectDataFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
