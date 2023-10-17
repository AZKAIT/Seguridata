import { Component, EventEmitter, OnDestroy } from '@angular/core';
import { WizardFormWrapper } from '../wizard-form-wrapper';
import { TableModel } from 'src/app/common/models/table-model';
import { Observable, Subscription } from 'rxjs';
import { TableService } from 'src/app/common/service/table.service';
import { ConnectionService } from 'src/app/common/service/connection.service';
import { MessageService } from 'primeng/api';
import { ConnectionModel } from 'src/app/common/models/connection-model';

@Component({
  selector: 'app-table-form-wrapper',
  templateUrl: './table-form-wrapper.component.html',
  styleUrls: ['./table-form-wrapper.component.css']
})
export class TableFormWrapperComponent implements WizardFormWrapper<TableModel>, OnDestroy {
  private _index: number = -2;
  private _name!: string;
  private _indexEmitter = new EventEmitter<number>();
  private _resultEmitter = new EventEmitter<TableModel>();

  private _subsList: Subscription[] = [];
  ongoingTable!: TableModel;

  private _inputConnection!: ConnectionModel;

  formLoading: boolean = false;

  constructor(private _connectionService: ConnectionService, private _tableService: TableService, private _messageService: MessageService) {
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

  getResult(): Observable<TableModel> {
    return this._resultEmitter.asObservable();
  }

  get inputConnection(): ConnectionModel {
    return this._inputConnection;
  }

  set inputConnection(inputConnection: ConnectionModel) {
    this._inputConnection = inputConnection;
  }

  saveTableData(table: TableModel) {
    this.formLoading = true;
    if (table?.id) {
      this._subsList.push(this._tableService.updateTable(table.id, table)
        .subscribe({
          next: updatedTable => {
            if (this.ongoingTable && updatedTable) {
              this.ongoingTable = updatedTable;
              this.postSuccess('Actualizar Tabla', `Tabla ${updatedTable?.name} actualizada`);
            }
            this.formLoading = false;
          },
          error: err => {
            this.formLoading = false;
            this.postError('Error al actualizar Tabla', err?.messages?.join(','));
          }
        }));
    } else if (this._inputConnection?.id) {
      this._subsList.push(this._connectionService.createTableForConnection(this._inputConnection.id, table)
        .subscribe({
          next: newTable => {
            if (newTable) {
              this.ongoingTable = newTable;
              this.postSuccess('Crear Tabla', `Tabla ${newTable?.name} creada`);
            }
            this.formLoading = false;
          },
          error: err => {
            this.formLoading = false;
            this.postError('Error al crear Tabla', err?.messages?.join(','));
          }
        }));
    }
  }

  private postError(title: string, message: string) {
    this._messageService.add({ severity: 'error', summary: title, detail: message });
  }

  private postSuccess(title: string, message: string) {
    this._messageService.add({ severity: 'success', summary: title, detail: message });
  }
}
