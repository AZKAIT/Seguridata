import { Component, EventEmitter } from '@angular/core';
import { WizardFormWrapper } from '../wizard-form-wrapper';
import { Observable, Subscription } from 'rxjs';
import { PlanModel } from 'src/app/common/models/plan-model';
import { ProjectModel } from 'src/app/common/models/project-model';
import { TableModel } from 'src/app/common/models/table-model';
import { ProjectService } from 'src/app/common/service/project.service';
import { PlanService } from 'src/app/common/service/plan.service';
import { ConnectionService } from 'src/app/common/service/connection.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-plan-form-wrapper',
  templateUrl: './plan-form-wrapper.component.html',
  styleUrls: ['./plan-form-wrapper.component.css']
})
export class PlanFormWrapperComponent implements WizardFormWrapper<PlanModel> {
  private _index: number = -2;
  private _name!: string;
  private _indexEmitter = new EventEmitter<number>();
  private _resultEmitter = new EventEmitter<PlanModel>();

  private _inputProject!: ProjectModel;

  private subsList: Subscription[] = [];

  ongoingPlan!: PlanModel;

  sourceTableList: TableModel[] = [];
  targetTableList: TableModel[] = [];
  formLoading: boolean = false;

  constructor(private _projectService: ProjectService, private _planService: PlanService,
    private _connService: ConnectionService, private _messageService: MessageService) {
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

  getResult(): Observable<PlanModel> {
    return this._resultEmitter.asObservable();
  }

  get inputProject(): ProjectModel {
    return this._inputProject;
  }

  set inputProject(inputProject: ProjectModel) {
    this._inputProject = inputProject;
    this.fetchConnTables(this._inputProject);
  }

  savePlanData(plan: PlanModel) {
    this.formLoading = true;
    if (plan?.id) {
      this.subsList.push(this._planService.updatePlan(plan.id, plan)
        .subscribe({
          next: updatedPlan => {
            if (this.ongoingPlan && updatedPlan) {
              this.ongoingPlan = updatedPlan;
              this.postSuccess('Actualizar Plan', 'Plan actualizado');
              this.emitNextIndex();
              this._resultEmitter.next(this.ongoingPlan);
            }
            this.formLoading = false;
          },
          error: err => {
            this.formLoading = false;
            this.postError('Error al actualizar Plan', err?.messages?.join(','));
          }
        }));
    } else if (this._inputProject?.id) {
      this.subsList.push(this._projectService.createPlanForProject(this._inputProject.id, plan)
        .subscribe({
          next: newPlan => {
            if (newPlan) {
              this.ongoingPlan = newPlan;
              this.postSuccess('Crear Plan', 'Plan creado');
              this.emitNextIndex();
              this._resultEmitter.next(this.ongoingPlan);
            }
            this.formLoading = false;
          },
          error: err => {
            this.formLoading = false;
            this.postError('Error al crear Plan', err?.messages?.join(','));
          }
        }));
    } else {
      this.postError('Error en Proyecto', 'No hay Proyecto definido');
      this.formLoading = false;
    }
  }

  private postError(title: string, message: string) {
    this._messageService.add({ severity: 'error', summary: title, detail: message });
  }

  private postSuccess(title: string, message: string) {
    this._messageService.add({ severity: 'success', summary: title, detail: message });
  }

  private fetchConnTables(project: ProjectModel | undefined) {
    if (!project) {
      this.sourceTableList = [];
      this.targetTableList = [];
      return;
    }

    if (project.sourceConnection?.id) {
      this.subsList.push(
        this._connService.getTablesForConnection(project.sourceConnection?.id)
          .subscribe({
            next: tableList => {
              this.sourceTableList = tableList ?? [];
            },
            error: err => {
              this.postError('Error en Tablas de Conexión origen', err?.messages?.join(','));
            }
          })
      );
    } else {
      this.postError('Error en Conexión', 'No hay Conexión origen definido');
    }

    if (project.targetConnection?.id) {
      this.subsList.push(
        this._connService.getTablesForConnection(project.targetConnection?.id)
          .subscribe({
            next: tableList => {
              this.targetTableList = tableList ?? [];
            },
            error: err => {
              this.postError('Error en Tablas de Conexión destino', err?.messages?.join(','));
            }
          })
      );
    } else {
      this.postError('Error en Conexión', 'No hay Conexión destino definido');
    }
  }
}
