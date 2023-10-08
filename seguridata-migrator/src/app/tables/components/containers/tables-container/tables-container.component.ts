import { Component, OnDestroy, Input } from '@angular/core';
import { Subscription } from 'rxjs';

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

  subsList: Subscription[] = [];

  _connection?: ConnectionModel;

  tableList: TableModel[] = [];
  selectedTable: TableModel | undefined;

  constructor(private _connectionService: ConnectionService, private _tableService: TableService) {
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
    console.log(`Execute onEditTable: ${JSON.stringify(this.selectedTable)}`);
  }

  onDeleteTable() {
    if (this.selectedTable?.id) {
      this.subsList.push(this._tableService.deleteTable(this.selectedTable.id)
        .subscribe(delTable => {
          if (this.selectedTable) {
            this.tableList.splice(this.tableList.indexOf(this.selectedTable, 1))
            this.selectedTable = undefined;
          }
        }));
    }
  }

  onCreateTable() {
    this.selectedTable = undefined;
  }

  saveTableData(table: TableModel) {
    if (table?.id) {
      this.subsList.push(this._tableService.updateTable(table.id, table)
        .subscribe(updatedTable => {
          if (this.selectedTable && updatedTable) {
            const prevDataIndex = this.tableList.indexOf(this.selectedTable);
            this.tableList[prevDataIndex] = updatedTable;
            this.selectedTable = undefined;
          }
        }));
    } else if (this._connection?.id) {
      this.subsList.push(this._connectionService.createTableForConnection(this._connection.id, table)
        .subscribe(newTable => {
          if (newTable) {
            this.tableList.push(newTable);
            this.selectedTable = undefined;
          }
        }));
    } else {
      console.error('No Connection or Connection ID');
    }
  }


  private fetchTables(connection: ConnectionModel | undefined) {
    if (connection?.id) {
      this.subsList.push(
        this._connectionService.getTablesForConnection(connection.id)
        .subscribe(tableList => {
          this.tableList = tableList ?? [];
        })
      );
    } else {
      this.tableList = [];
      this.selectedTable = undefined;
    }
  }
}
