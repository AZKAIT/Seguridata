import { Component, EventEmitter } from '@angular/core';
import { WizardFormWrapper } from '../wizard-form-wrapper';
import { Observable, Subscription } from 'rxjs';
import { DefinitionModel } from 'src/app/common/models/definition-model';
import { PlanModel } from 'src/app/common/models/plan-model';
import { PlanService } from 'src/app/common/service/plan.service';
import { DefinitionService } from 'src/app/common/service/definition.service';
import { MessageService } from 'primeng/api';
import { TableService } from 'src/app/common/service/table.service';
import { ColumnModel } from 'src/app/common/models/column-model';

@Component({
  selector: 'app-definition-form-wrapper',
  templateUrl: './definition-form-wrapper.component.html',
  styleUrls: ['./definition-form-wrapper.component.css']
})
export class DefinitionFormWrapperComponent implements WizardFormWrapper<DefinitionModel[]> {
  private _index: number = -2;
  private _name!: string;
  private _indexEmitter = new EventEmitter<number>();
  private _resultEmitter = new EventEmitter<DefinitionModel[]>();

  private _inputPlan!: PlanModel;
  private _subsList: Subscription[] = [];

  sourceColumnList: ColumnModel[] = [];
  targetColumnList: ColumnModel[] = [];

  ongoingDefs!: DefinitionModel[];

  formLoading: boolean = false;

  constructor(private _planService: PlanService, private _defService: DefinitionService,
    private _tableService: TableService, private _messageService: MessageService) {
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

  getResult(): Observable<DefinitionModel[]> {
    return this._resultEmitter.asObservable();
  }

  get inputPlan(): PlanModel {
    return this._inputPlan;
  }

  set inputPlan(inputPlan: PlanModel) {
    this._inputPlan = inputPlan;
    this.fetchTableColumns(this._inputPlan);
  }

  saveDefinitionsBatch(defs: DefinitionModel[]) {
    this.formLoading = true;
    if (this._inputPlan?.id) {
      this._subsList.push(this._planService.createDefinitionsBatchForPlan(this._inputPlan.id, defs)
        .subscribe({
          next: newDefs => {
            if (newDefs) {
              this.ongoingDefs = newDefs;
              this.postSuccess('Crear Definiciones', 'Definiciones creadas');
              this.emitNextIndex();
              this._resultEmitter.next(this.ongoingDefs);
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
