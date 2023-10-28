import { Component, EventEmitter, Input, Output } from '@angular/core';
import { TableRowSelectEvent, TableRowUnSelectEvent } from 'primeng/table';
import { JobStatus } from 'src/app/common/enums/job-status';
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

  @Output() executeProjectEvent = new EventEmitter<void>();
  @Output() stopProjectEvent = new EventEmitter<void>();

  @Input() selectedProject?: ProjectModel;
  @Output() selectedProjectChange = new EventEmitter<ProjectModel | undefined>();

  @Input() tableLoading?: boolean;
  @Input() deleteLoading?: boolean;
  @Input() schedulingLoading?: boolean;

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

  onRowSelect(event : TableRowSelectEvent) {
    this.selectedProjectChange.emit(this.selectedProject);
  }

  onRowUnselect(event : TableRowUnSelectEvent) {
    this.selectedProjectChange.emit(this.selectedProject);
  }

  onScheduleProject() {
    if (this.isExecutable(this.selectedProject)) {
      this.executeProjectEvent.emit();
    } else {
      this.stopProjectEvent.emit();
    }
  }

  isExecutable(project: ProjectModel | undefined): boolean {
    if (!project) {
      return false;
    }

    return !project.locked;
  }

  parseStatus(status: any): JobStatus | string {
    let projStatus = status;
    if (typeof projStatus === 'string') {
      projStatus = JobStatus[JobStatus[projStatus as keyof typeof JobStatus]];
    } else if (typeof projStatus === 'number') {
      projStatus = JobStatus[status]
    }
    return projStatus;
  }
}
