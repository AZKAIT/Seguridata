import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ProjectStatus } from 'src/app/common/enums/project-status';
import { ProjectModel } from 'src/app/common/models/project-model';

@Component({
  selector: 'app-project-list',
  templateUrl: './project-list.component.html',
  styleUrls: ['./project-list.component.css']
})
export class ProjectListComponent {

  @Input() projectList?: ProjectModel[];
  @Output() listRefreshEvent = new EventEmitter<void>();
  @Output() editProjectEvent = new EventEmitter<void>();
  @Output() deleteProjectEvent = new EventEmitter<void>();
  @Output() createProjectEvent = new EventEmitter<void>();

  @Output() executeProjectEvent = new EventEmitter<string>();
  @Output() stopProjectEvent = new EventEmitter<string>();

  @Input() selectedProject?: ProjectModel;
  @Output() selectedProjectChange = new EventEmitter<ProjectModel | undefined>();

  refreshList() {
    this.listRefreshEvent.emit();
  }

  editProject(): void {
    this.editProjectEvent.next();
  }

  deleteProject(): void {
    this.deleteProjectEvent.next();
  }

  createProject(): void {
    this.createProjectEvent.next();
  }


  onSelectedProjectFromList(project: ProjectModel) {
    this.selectedProject = project;
    this.selectedProjectChange.emit(this.selectedProject);
  }

  onExecuteProject(projectId: string) {
    this.executeProjectEvent.emit(projectId);
  }

  onStopProject(projectId: string) {
    this.stopProjectEvent.emit(projectId);
  }

  isExecutable(project: ProjectModel): boolean {
    let projStatus = project.status;
    if (typeof projStatus === 'string') {
      projStatus = ProjectStatus[projStatus as keyof typeof ProjectStatus];
    }

    return (ProjectStatus.CREATED === projStatus || ProjectStatus.STOPPED == projStatus);
  }
}
