import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { TableRowSelectEvent, TableRowUnSelectEvent } from 'primeng/table';
import { ProjectModel } from 'src/app/common/models/project-model';

@Component({
  selector: 'app-project-list',
  templateUrl: './project-list.component.html',
  styleUrls: ['./project-list.component.css']
})
export class ProjectListComponent implements OnChanges {

  private _projectList?: ProjectModel[];
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

  firstIndex = 0;
  numTables = 0;

  @Input()
  get projectList() {
    return this._projectList;
  }
  set projectList(projectList: ProjectModel[] | undefined) {
    this._projectList = projectList;
    this.firstIndex = 0;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this._projectList) {
      this.numTables = this._projectList.length;
    } else {
      this.numTables = 0;
    }
  }

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
}
