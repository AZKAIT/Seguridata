import { Component, EventEmitter } from '@angular/core';
import { MessageService } from 'primeng/api';
import { Observable, Subscription } from 'rxjs';
import { ColumnModel } from 'src/app/common/models/column-model';
import { TableModel } from 'src/app/common/models/table-model';
import { ColumnService } from 'src/app/common/service/column.service';
import { TableService } from 'src/app/common/service/table.service';

@Component({
  selector: 'app-column-form-wrapper',
  templateUrl: './column-form-wrapper.component.html',
  styleUrls: ['./column-form-wrapper.component.css']
})
export class ColumnFormWrapperComponent {
  private _index: number = -2;
  private _name!: string;
  private _indexEmitter = new EventEmitter<number>();
  private _resultEmitter = new EventEmitter<ColumnModel>();

  private _subsList: Subscription[] = [];
  ongoingColumn!: ColumnModel;

  private _inputTable!: TableModel;

  formLoading: boolean = false;

  constructor(private _tableService: TableService, private _columnService: ColumnService, private _messageService: MessageService) {
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

  getResult(): Observable<ColumnModel> {
    return this._resultEmitter.asObservable();
  }

  get inputTable(): TableModel {
    return this._inputTable;
  }

  set inputTable(inputTable: TableModel) {
    this._inputTable = inputTable;
  }

  saveColumnData(column: ColumnModel) {
    this.formLoading = true;
    if (column?.id) {
      this._subsList.push(this._columnService.updateColumn(column.id, column)
        .subscribe({
          next: updatedCol => {
            if (this.ongoingColumn && updatedCol) {
              this.ongoingColumn = updatedCol;
              this.postSuccess('Actualizar Columna', `Columna ${updatedCol?.name} actualizada`);
            }
            this.formLoading = false;
          },
          error: err => {
            this.formLoading = false;
            this.postError('Error actualizando Columna', err?.messages?.join(','));
          }
        }));
    } else if (this._inputTable?.id) {
      this._subsList.push(this._tableService.createColumnForTable(this._inputTable.id, column)
        .subscribe({
          next: newColumn => {
            if (newColumn) {
              this.ongoingColumn = newColumn;
              this.postSuccess('Crear Columna', `Columna ${newColumn?.name} creada`);
            }
            this.formLoading = false;
          },
          error: err => {
            this.formLoading = false;
            this.postError('Error creando Columna', err?.messages?.join(','));
          }
        }));
    } else {
      this.postError('Error en la Tabla', 'No hay una Tabla definida');
      this.formLoading = false;
    }
  }

  private postError(title: string, message: string) {
    this._messageService.add({ severity: 'error', summary: title, detail: message });
  }

  private postSuccess(title: string, message: string) {
    this._messageService.add({ severity: 'success', summary: title, detail: message });
  }
}
