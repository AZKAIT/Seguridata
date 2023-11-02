import { Component, Input, OnDestroy } from '@angular/core';
import { MessageService, ConfirmationService } from 'primeng/api';
import { Subscription } from 'rxjs';
import { ColumnModel } from 'src/app/common/models/column-model';
import { DefinitionModel } from 'src/app/common/models/definition-model';

import { PlanModel } from 'src/app/common/models/plan-model';
import { DefinitionService } from 'src/app/common/service/definition.service';
import { PlanService } from 'src/app/common/service/plan.service';
import { TableService } from 'src/app/common/service/table.service';

@Component({
  selector: 'app-definitions-container',
  templateUrl: './definitions-container.component.html',
  styleUrls: ['./definitions-container.component.css']
})
export class DefinitionsContainerComponent implements OnDestroy {

  private _subsList: Subscription[] = [];

  _plan: PlanModel | undefined;

  defList: DefinitionModel[] = [];
  sourceColumnList: ColumnModel[] = [];
  targetColumnList: ColumnModel[] = [];

  selectedDef: DefinitionModel | undefined;

  showUpdateForm: boolean = false;
  showCreateForm: boolean = false;

  tableLoading: boolean = false;
  formLoading: boolean = false;
  deleteLoading: boolean = false;


  constructor(private _planService: PlanService, private _defService: DefinitionService,
    private _tableService: TableService, private _messageService: MessageService, private _confirmService: ConfirmationService) {
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
  get plan(): PlanModel | undefined { return this._plan; }
  set plan(plan: PlanModel | undefined) {
    this._plan = plan;
    this.fetchDefinitions(this._plan);
    this.fetchTableColumns(this._plan);
  }

  onExecuteRefresh() {
    this.fetchDefinitions(this._plan);
    this.fetchTableColumns(this._plan);
  }

  onEditDefinition() {
    this.showUpdateForm = true;
  }

  onDeleteDef() {
    this._confirmService.confirm({
      message: `¿Desea eliminar la Definición para las Columnas [${this.selectedDef?.sourceColumn?.name}] -> [${this.selectedDef?.targetColumn?.name}]?`,
      header: 'Eliminar Definición',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.deleteLoading = true;
        if (this.selectedDef?.id) {
          this._subsList.push(this._defService.deleteDefinition(this.selectedDef.id)
            .subscribe({
              next: delDef => {
                if (this.selectedDef) {
                  this.defList.splice(this.defList.indexOf(this.selectedDef), 1);
                  this.selectedDef = undefined;
                  this.postSuccess('Eliminar Definición', 'Definición eliminada');
                }
                this.deleteLoading = false;
              },
              error: err => {
                this.postError('Error al eliminar Definición', err?.messages?.join(','));
                this.deleteLoading = false;
              }
            }));
        }
      }
    });
  }

  onCreateDef() {
    this.selectedDef = undefined;
    this.showCreateForm = true;
  }

  saveDefData(def: DefinitionModel) {
    this.formLoading = true;
    if (def?.id) {
      this._subsList.push(this._defService.updateDefinition(def.id, def)
        .subscribe({
          next: updatedDef => {
            if (this.selectedDef && updatedDef) {
              const prevDataIndex = this.defList.indexOf(this.selectedDef);
              this.defList[prevDataIndex] = updatedDef;
              this.selectedDef = undefined;
              this.showUpdateForm = false;
              this.postSuccess('Actualizar Definición', 'Definición actualizada');
            }
            this.formLoading = false;
          },
          error: err => {
            this.postError('Error al actualizar Definición', err?.messages?.join(','));
            this.formLoading = false;
          }
        }));
    } else if (this._plan?.id) {
      this._subsList.push(this._planService.createDefinitionForPlan(this._plan.id, def)
        .subscribe({
          next: newDef => {
            if (newDef) {
              this.defList.push(newDef);
              this.selectedDef = undefined;
              this.showCreateForm = false;
              this.postSuccess('Crear Definición', 'Definición creada');
            }
            this.formLoading = false;
          },
          error: err => {
            this.postError('Error al crear Definición', err?.messages?.join(','));
            this.formLoading = false;
          }
        }));
    } else {
      this.postError('Error en el Plan', 'No hay un Plan definido');
      this.formLoading = false;
    }
  }

  saveDefinitionsBatch(defs: DefinitionModel[]) {
    this.formLoading = true;
    if (this._plan?.id) {
      this._subsList.push(this._planService.createDefinitionsBatchForPlan(this._plan.id, defs)
        .subscribe({
          next: newDefs => {
            if (newDefs) {
              this.defList.push(...newDefs);
              this.selectedDef = undefined;
              this.showCreateForm = false;
              this.postSuccess('Crear Definiciones', 'Definiciones creadas');
            }
            this.formLoading = false;
          },
          error: err => {
            this.postError('Error al crear lista de Definiciones', err?.messages?.join(','));
            this.formLoading = false;
          }
        }))
    } else {
      this.postError('Error en el Plan', 'No hay un Plan definido');
      this.formLoading = false;
    }
  }


  private fetchDefinitions(plan: PlanModel | undefined) {
    this.selectedDef = undefined;
    this.tableLoading = true;
    if (plan?.id) {
      this._subsList.push(
        this._planService.getDefinitionsForPlan(plan.id)
          .subscribe({
            next: defList => {
              this.defList = defList ?? [];
              this.tableLoading = false;
            },
            error: err => {
              this.postError('Error en Definiciones', err?.messages?.join(','));
              this.tableLoading = false;
            }
          })
      );
    } else {
      this.defList = [];
      this.selectedDef = undefined;
      this.tableLoading = false;
    }
  }

  private fetchTableColumns(plan: PlanModel | undefined) {
    if (!plan) {
      this.sourceColumnList = [];
      this.targetColumnList = [];
      return;
    }

    if (plan.sourceTable?.id) {
      this._subsList.push(
        this._tableService.getColumnsForTable(plan.sourceTable?.id)
          .subscribe({
            next: columnList => {
              this.sourceColumnList = columnList ?? [];
            },
            error: err => {
              this.postError('Error en Columnas de Tabla origen', err?.messages?.join(','));
            }
          })
      );
    } else {
      this.postError('Error en la Tabla', 'No hay una Tabla origen definida');
    }

    if (plan.targetTable?.id) {
      this._subsList.push(
        this._tableService.getColumnsForTable(plan.targetTable?.id)
          .subscribe({
            next: columnList => {
              this.targetColumnList = columnList ?? [];
            },
            error: err => {
              this.postError('Error en Columnas de Tabla destino', err?.messages?.join(','));
            }
          })
      );
    } else {
      this.postError('Error en la Tabla', 'No hay una Tabla destino definida');
    }
  }

  private postError(title: string, message: string) {
    this._messageService.add({ severity: 'error', summary: title, detail: message });
  }

  private postSuccess(title: string, message: string) {
    this._messageService.add({ severity: 'success', summary: title, detail: message });
  }
}
