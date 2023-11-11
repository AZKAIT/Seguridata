import { Component, OnDestroy, Input } from '@angular/core';
import { MessageService, ConfirmationService } from 'primeng/api';
import { Subscription } from 'rxjs';
import { ColumnModel } from 'src/app/common/models/column-model';
import { TableModel } from 'src/app/common/models/table-model';
import { ColumnService } from 'src/app/common/service/column.service';
import { TableService } from 'src/app/common/service/table.service';

@Component({
  selector: 'app-columns-container',
  templateUrl: './columns-container.component.html',
  styleUrls: ['./columns-container.component.css']
})
export class ColumnsContainerComponent implements OnDestroy {

  private _subsList: Subscription[] = [];

  _table: TableModel | undefined;

  columnList: ColumnModel[] = [];
  selectedColumn: ColumnModel | undefined;

  showForm: boolean = false;

  tableLoading: boolean = false;
  formLoading: boolean = false;
  deleteLoading: boolean = false;

  constructor(private _tableService: TableService, private _columnService: ColumnService, private _messageService: MessageService, private _confirmService: ConfirmationService) {
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
  get table(): TableModel | undefined { return this._table; }
  set table(table: TableModel | undefined) {
    this._table = table;
    this.fetchColumns(this._table);
  }

  onExecuteRefresh() {
    this.fetchColumns(this._table);
  }

  onEditColumn() {
    this.showForm = true;
  }

  onDeleteColumn() {
    this._confirmService.confirm({
      message: `¿Desea eliminar la Columna${this.selectedColumn? ' ' + this.selectedColumn.name : ''}?`,
      header: 'Eliminar Columna',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.deleteLoading = true;
        if (this.selectedColumn?.id) {
          this._subsList.push(this._columnService.deleteColumn(this.selectedColumn.id)
            .subscribe({
              next: delColumn => {
                if (this.selectedColumn) {
                  this.columnList.splice(this.columnList.findIndex(col => col.id == this.selectedColumn?.id), 1);
                  this.selectedColumn = undefined;
                  this.postSuccess('Eliminar Columna', `Columna ${delColumn?.name} eliminada`);
                }
                this.deleteLoading = false;
              },
              error: err => {
                this.deleteLoading = false;
                this.postError('Error eliminando Columna', err?.messages?.join(','));
              }
            }));
        }
      }
    });
  }

  onCreateColumn() {
    this.selectedColumn = undefined;
    this.showForm = true;
  }

  saveColumnData(column: ColumnModel) {
    this.formLoading = true;
    if (column?.id) {
      this._subsList.push(this._columnService.updateColumn(column.id, column)
        .subscribe({
          next: updatedCol => {
            if (this.selectedColumn && updatedCol) {
              const prevDataIndex = this.columnList.findIndex(col => col.id == this.selectedColumn?.id);
              this.columnList[prevDataIndex] = updatedCol;
              this.selectedColumn = undefined;
              this.showForm = false;
              this.postSuccess('Actualizar Columna', `Columna ${updatedCol?.name} actualizada`);
            }
            this.formLoading = false;
          },
          error: err => {
            this.formLoading = false;
            this.postError('Error actualizando Columna', err?.messages?.join(','));
          }
        }));
    } else if (this._table?.id) {
      this._subsList.push(this._tableService.createColumnForTable(this._table.id, column)
        .subscribe({
          next: newColumn => {
            if (newColumn) {
              this.columnList.push(newColumn);
              this.selectedColumn = undefined;
              this.showForm = false;
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


  private fetchColumns(table: TableModel | undefined) {
    this.selectedColumn = undefined;
    this.tableLoading = true;
    if (table?.id) {
      this._subsList.push(
        this._tableService.getColumnsForTable(table.id)
          .subscribe({
            next: columnList => {
              this.columnList = columnList ?? [];
              this.tableLoading = false;
            },
            error: err => {
              this.tableLoading = false;
              this.postError('Error cargando Columnas', err?.messages?.join(','));
            }
          })
      );
    } else {
      this.columnList = [];
      this.selectedColumn = undefined;
      this.tableLoading = false;
    }
  }

  private postError(title: string, message: string) {
    this._messageService.add({ severity: 'error', summary: title, detail: message });
  }

  private postSuccess(title: string, message: string) {
    this._messageService.add({ severity: 'success', summary: title, detail: message });
  }
}
