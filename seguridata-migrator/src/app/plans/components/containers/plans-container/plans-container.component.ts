import { Component, Input, OnDestroy } from '@angular/core';
import { MessageService, ConfirmationService } from 'primeng/api';
import { Subscription } from 'rxjs';
import { PlanModel } from 'src/app/common/models/plan-model';

import { ProjectModel } from 'src/app/common/models/project-model';
import { TableModel } from 'src/app/common/models/table-model';
import { ConnectionService } from 'src/app/common/service/connection.service';
import { PlanService } from 'src/app/common/service/plan.service';
import { ProjectService } from 'src/app/common/service/project.service';

@Component({
  selector: 'app-plans-container',
  templateUrl: './plans-container.component.html',
  styleUrls: ['./plans-container.component.css']
})
export class PlansContainerComponent implements OnDestroy {

  private _subsList: Subscription[] = [];

  _project: ProjectModel | undefined;

  planList: PlanModel[] = [];
  sourceTableList: TableModel[] = [];
  targetTableList: TableModel[] = [];

  selectedPlan: PlanModel | undefined;

  showForm: boolean = false;

  tableLoading: boolean = false;
  formLoading: boolean = false;
  deleteLoading: boolean = false;

  constructor(private _projectService: ProjectService, private _planService: PlanService,
    private _connService: ConnectionService, private _messageService: MessageService, private _confirmService: ConfirmationService) {
  }

  ngOnDestroy(): void {
    let subs: Subscription | undefined;
    while (this._subsList.length) {
      subs = this._subsList.pop();
      if (subs) {
        subs.unsubscribe();
      }
    }
  }

  @Input()
  get project(): ProjectModel | undefined { return this._project; }
  set project(project: ProjectModel | undefined) {
    this._project = project;
    this.fetchPlans(this._project);
    this.fetchConnTables(this._project);
  }

  onExecuteRefresh() {
    this.fetchPlans(this._project);
    this.fetchConnTables(this._project);
  }

  onEditPlan() {
    this.showForm = true;
  }

  onDeletePlan() {
    this._confirmService.confirm({
      message: `¿Desea eliminar el Plan?`,
      header: 'Eliminar Plan',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.deleteLoading = true;
        if (this.selectedPlan?.id) {
          this._subsList.push(this._planService.deletePlan(this.selectedPlan.id)
            .subscribe({
              next: delPlan => {
                if (this.selectedPlan) {
                  this.planList.splice(this.planList.indexOf(this.selectedPlan), 1);
                  this.selectedPlan = undefined;
                  this.postSuccess('Eliminar Plan', 'Plan eliminado');
                }
                this.deleteLoading = false;
              },
              error: err => {
                this.deleteLoading = false;
                this.postError('Error al eliminar Plan', err?.messages?.join(','));
              }
            }));
        }
      }
    });
  }

  onCreatePlan() {
    this.selectedPlan = undefined;
    this.showForm = true;
  }

  savePlanData(plan: PlanModel) {
    this.formLoading = true;
    if (plan?.id) {
      this._subsList.push(this._planService.updatePlan(plan.id, plan)
        .subscribe({
          next: updatedPlan => {
            if (this.selectedPlan && updatedPlan) {
              const prevDataIndex = this.planList.indexOf(this.selectedPlan);
              this.planList[prevDataIndex] = updatedPlan;
              this.selectedPlan = undefined;
              this.showForm = false;
              this.postSuccess('Actualizar Plan', 'Plan actualizado');
            }
            this.formLoading = false;
          },
          error: err => {
            this.formLoading = false;
            this.postError('Error al actualizar Plan', err?.messages?.join(','));
          }
        }));
    } else if (this._project?.id) {
      this._subsList.push(this._projectService.createPlanForProject(this._project.id, plan)
        .subscribe({
          next: newPlan => {
            if (newPlan) {
              this.planList.push(newPlan);
              this.selectedPlan = undefined;
              this.showForm = false;
              this.postSuccess('Crear Plan', 'Plan creado');
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



  private fetchPlans(project: ProjectModel | undefined) {
    this.selectedPlan = undefined;
    this.tableLoading = true;
    if (project?.id) {
      this._subsList.push(
        this._projectService.getPlansForProject(project.id)
          .subscribe({
            next: planList => {
              this.planList = planList ?? [];
              this.tableLoading = false;
            },
            error: err => {
              this.tableLoading = false;
              this.postError('Error al cargar Planes', err?.messages?.join(','));
            }
          })
      );
    } else {
      this.planList = [];
      this.selectedPlan = undefined;
      this.tableLoading = false;
    }
  }

  private fetchConnTables(project: ProjectModel | undefined) {
    if (!project) {
      this.sourceTableList = [];
      this.targetTableList = [];
      return;
    }

    if (project.sourceConnection?.id) {
      this._subsList.push(
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
      this._subsList.push(
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

  private postError(title: string, message: string) {
    this._messageService.add({ severity: 'error', summary: title, detail: message });
  }

  private postSuccess(title: string, message: string) {
    this._messageService.add({ severity: 'success', summary: title, detail: message });
  }
}
