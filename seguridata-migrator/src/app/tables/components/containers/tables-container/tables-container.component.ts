import { Component, OnDestroy, Input } from '@angular/core';
import { MessageService, ConfirmationService } from 'primeng/api';
import { Subscription } from 'rxjs';
import { ColumnModel } from 'src/app/common/models/column-model';

import { ConnectionModel } from 'src/app/common/models/connection-model';
import { TableModel } from 'src/app/common/models/table-model';
import { ConnectionService } from 'src/app/common/service/connection.service';
import { TableService } from 'src/app/common/service/table.service';

@Component({
  selector: 'app-tables-container',
  templateUrl: './tables-container.component.html',
  styleUrls: ['./tables-container.component.css']
})
export class TablesContainerComponent implements OnDestroy {

  private _subsList: Subscription[] = [];

  _connection?: ConnectionModel;

  tableList: TableModel[] = [];
  selectedTable: TableModel | undefined;

  showForm: boolean = false;

  tableLoading: boolean = false;
  formLoading: boolean = false;
  deleteLoading: boolean = false;

  constructor(private _connectionService: ConnectionService, private _tableService: TableService, private _messageService: MessageService, private _confirmService: ConfirmationService) {
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
  get connection(): ConnectionModel | undefined { return this._connection; }
  set connection(connection: ConnectionModel | undefined) {
    this._connection = connection;
    this.fetchTables(this._connection);
  }


  onExecuteRefresh() {
    this.fetchTables(this._connection);
  }

  onEditTable() {
    this.showForm = true;
  }

  onDeleteTable() {
    this._confirmService.confirm({
      message: `¿Desea eliminar la Tabla${this.selectedTable? ' ' + this.selectedTable.name : ''}?`,
      header: 'Eliminar Tabla',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.deleteLoading = true;
        if (this.selectedTable?.id) {
          this._subsList.push(this._tableService.deleteTable(this.selectedTable.id)
            .subscribe({
              next: delTable => {
                if (this.selectedTable) {
                  this.tableList.splice(this.tableList.indexOf(this.selectedTable), 1);
                  this.selectedTable = undefined;
                  this.postSuccess('Eliminar Tabla', `Tabla ${delTable?.name} eliminada`);
                }
                this.deleteLoading = false;
              },
              error: err => {
                this.deleteLoading = false;
                this.postError('Error al eliminar Tabla', err?.messages?.join(','));
              }
            }));
        }
      }
    });

  }

  onCreateTable() {
    this.selectedTable = undefined;
    this.showForm = true;
    this.columnList = [];
  }

  saveTableData(table: TableModel) {
    this.formLoading = true;
    if (table?.id) {
      this._subsList.push(this._tableService.updateTable(table.id, table)
        .subscribe({
          next: updatedTable => {
            if (this.selectedTable && updatedTable) {
              const prevDataIndex = this.tableList.indexOf(this.selectedTable);
              this.tableList[prevDataIndex] = updatedTable;
              this.selectedTable = undefined;
              this.showForm = false;
              this.postSuccess('Actualizar Tabla', `Tabla ${updatedTable?.name} actualizada`);
            }
            this.formLoading = false;
          },
          error: err => {
            this.formLoading = false;
            this.postError('Error al actualizar Tabla', err?.messages?.join(','));
          }
        }));
    } else if (this._connection?.id) {
      this._subsList.push(this._connectionService.createTableForConnection(this._connection.id, table)
        .subscribe({
          next: newTable => {
            if (newTable) {
              this.tableList.push(newTable);
              this.selectedTable = undefined;
              this.showForm = false;
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

  // TODO: Delete when getting rid for order_column
  columnList: ColumnModel[] = [];
  fetchTableColumns() {
    if (this.selectedTable?.id) {
      this._subsList.push(this._tableService.getColumnsForTable(this.selectedTable.id)
        .subscribe({
          next: colList => {
            if (colList) {
              this.columnList = colList;
            }
          },
          error: err => {
            this.postError('Error al obtener las Columnas de la tabla', err?.messages?.join(','));
          }
        }));
    }
  }

  private fetchTables(connection: ConnectionModel | undefined) {
    this.selectedTable = undefined;
    this.tableLoading = true;
    if (connection?.id) {
      this._subsList.push(
        this._connectionService.getTablesForConnection(connection.id)
          .subscribe({
            next: tableList => {
              this.tableList = tableList ?? [];
              this.tableLoading = false;
            },
            error: err => {
              this.tableLoading = false;
              this.postError('Error en Conexión', err?.messages?.join(','));
            }
          })
      );
    } else {
      this.tableList = [];
      this.selectedTable = undefined;
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
