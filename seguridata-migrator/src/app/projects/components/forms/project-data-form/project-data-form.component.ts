import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DropdownChangeEvent } from 'primeng/dropdown';
import { ConnectionModel } from 'src/app/common/models/connection-model';
import { ProjectModel } from 'src/app/common/models/project-model';

@Component({
  selector: 'app-project-data-form',
  templateUrl: './project-data-form.component.html',
  styleUrls: ['./project-data-form.component.css']
})
export class ProjectDataFormComponent {

  @Input() connectionList: ConnectionModel[] = [];

  @Output() saveProject = new EventEmitter<ProjectModel>();

  @Input() formLoading?: boolean;
  @Input() showForm?: boolean;

  selSourceConn?: ConnectionModel;
  selTargetConn?: ConnectionModel;

  _project?: ProjectModel;

  projectFormGroup: FormGroup;

  constructor(private _formBuilder: FormBuilder) {
    this.projectFormGroup = this._formBuilder.group({
      id: [''],
      name: ['', Validators.required],
      description: [''],
      sourceConnection: [undefined, Validators.required],
      targetConnection: [undefined, Validators.required],
      autoPopulate: [false],
      parallelThreads: [1, Validators.required]
    });
  }

  @Input()
  get project(): ProjectModel | undefined { return this._project; }
  set project(project: ProjectModel | undefined) {
    this._project = project;
    if (project) {
      this.projectFormGroup.patchValue(project);
      if (project.locked) {
        this.projectFormGroup.disable();
      }
      this.selSourceConn = project.sourceConnection;
      this.selTargetConn = project.targetConnection;
    } else {
      this.projectFormGroup.reset();
      this.projectFormGroup.patchValue({ sourceConnection: undefined, targetConnection: undefined });
    }
  }

  changeSourceConn(ddEvent: DropdownChangeEvent): void {
    this.selSourceConn = ddEvent.value;
  }

  changeTargetConn(ddEvent: DropdownChangeEvent): void {
    this.selTargetConn = ddEvent.value;
  }

  submit() {
    this.saveProject.emit(this.projectFormGroup.value);
  }
}
