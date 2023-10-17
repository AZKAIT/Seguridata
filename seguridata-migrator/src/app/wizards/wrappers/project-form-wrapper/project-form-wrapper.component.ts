import { Component, EventEmitter, OnDestroy, OnInit } from '@angular/core';
import { WizardFormWrapper } from '../wizard-form-wrapper';
import { Observable, Subscription } from 'rxjs';
import { ProjectService } from 'src/app/common/service/project.service';
import { ConnectionService } from 'src/app/common/service/connection.service';
import { MessageService } from 'primeng/api';
import { ConnectionModel } from 'src/app/common/models/connection-model';
import { ProjectModel } from 'src/app/common/models/project-model';

@Component({
  selector: 'app-project-form-wrapper',
  templateUrl: './project-form-wrapper.component.html',
  styleUrls: ['./project-form-wrapper.component.css']
})
export class ProjectFormWrapperComponent implements WizardFormWrapper<ProjectModel>, OnInit, OnDestroy {
  private _index: number = -2;
  private _name!: string;
  private _indexEmitter = new EventEmitter<number>();
  private _resultEmitter = new EventEmitter<ProjectModel>();

  subsList: Subscription[] = [];
  connList: ConnectionModel[] = [];
  ongoingProject!: ProjectModel;

  formLoading: boolean = false;

  constructor(private _projectService: ProjectService, private _connService: ConnectionService, private _messageService: MessageService) { }

  ngOnInit(): void {
    this.fetchConnections();
  }

  ngOnDestroy(): void {
    let subs: Subscription | undefined;
    while (this.subsList.length) {
      subs = this.subsList.pop();
      if (subs) {
        subs.unsubscribe();
      }
    }
  }

  setIndex(index: number): void {
    this._index = index;
  }

  getIndexEmitter(): Observable<number> {
    return this._indexEmitter.asObservable();
  }

  emitNextIndex(): void {
    this._indexEmitter.next(this._index + 1);
  }

  emitPreviousIndex(): void {
    this._indexEmitter.next(this._index - 1);
  }

  getName(): string {
      return this._name;
  }

  setName(name: string): void {
      this._name = name;
  }

  getResult(): Observable<ProjectModel> {
    return this._resultEmitter.asObservable();
  }

  saveProjectData(project: ProjectModel) {
    this.formLoading = true;
    if (project?.id) {
      this.subsList.push(this._projectService.updateProject(project.id, project)
        .subscribe({
          next: updatedProj => {
            if (this.ongoingProject && updatedProj) {
              this.ongoingProject = updatedProj;
              this.postSuccess('Actualizar Proyecto', `Proyecto ${updatedProj?.name} actualizado`);
              this.emitNextIndex();
              this._resultEmitter.next(this.ongoingProject);
            }
            this.formLoading = false;
          },
          error: err => {
            this.formLoading = false;
            this.postError('Error al actualizar Proyecto', err?.messages?.join(','));
          }
        }));
    } else {
      this.subsList.push(this._projectService.createProject(project)
        .subscribe({
          next: newProj => {
            if (newProj) {
              this.ongoingProject = newProj;
              this.postSuccess('Crear Proyecto', `Proyecto ${newProj?.name} creado`);
              this.emitNextIndex();
              this._resultEmitter.next(this.ongoingProject);
            }
            this.formLoading = false;
          },
          error: err => {
            this.formLoading = false;
            this.postError('Error al crear Proyecto', err?.messages?.join(','));
          }
        }));
    }
  }

  private fetchConnections() {
    this.subsList.push(this._connService.getAllConnections()
      .subscribe({
        next: connList => {
          this.connList = connList ?? [];
        },
        error: err => {
          this.postError('Error al cargar Conexiones', err?.messages?.join(','));
        }
      }));
  }

  private postError(title: string, message: string) {
    this._messageService.add({ severity: 'error', summary: title, detail: message });
  }

  private postSuccess(title: string, message: string) {
    this._messageService.add({ severity: 'success', summary: title, detail: message });
  }
}
