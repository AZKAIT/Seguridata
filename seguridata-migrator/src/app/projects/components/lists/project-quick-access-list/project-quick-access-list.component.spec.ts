import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectQuickAccessListComponent } from './project-quick-access-list.component';

describe('ProjectQuickAccessListComponent', () => {
  let component: ProjectQuickAccessListComponent;
  let fixture: ComponentFixture<ProjectQuickAccessListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProjectQuickAccessListComponent]
    });
    fixture = TestBed.createComponent(ProjectQuickAccessListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
