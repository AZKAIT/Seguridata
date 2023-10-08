import { Component, OnDestroy, Input } from '@angular/core';
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

  subsList: Subscription[] = [];

  _table: TableModel | undefined;

  columnList: ColumnModel[] = [];
  selectedColumn: ColumnModel | undefined;

  constructor(private _tableService: TableService, private _columnService: ColumnService) {
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
  get table(): TableModel | undefined { return this._table; }
  set table(table: TableModel | undefined) {
    this._table = table;
    this.fetchColumns(this._table);
  }

  onExecuteRefresh() {
    this.fetchColumns(this._table);
  }

  onEditColumn() {
    console.log(`Execute onColumnDataEvent: ${JSON.stringify(this.selectedColumn)}`);
  }

  onDeleteColumn() {
    if (this.selectedColumn?.id) {
      this.subsList.push(this._columnService.deleteColumn(this.selectedColumn.id)
        .subscribe(delColumn => {
          if (this.selectedColumn) {
            this.columnList.splice(this.columnList.indexOf(this.selectedColumn, 1))
            this.selectedColumn = undefined;
          }
        }));
    }
  }

  onCreateColumn() {
    this.selectedColumn = undefined;
  }

  saveColumnData(column: ColumnModel) {
    if (column?.id) {
      this.subsList.push(this._columnService.updateColumn(column.id, column)
        .subscribe(updatedCol => {
          if (this.selectedColumn && updatedCol) {
            const prevDataIndex = this.columnList.indexOf(this.selectedColumn);
            this.columnList[prevDataIndex] = updatedCol;
            this.selectedColumn = undefined;
          }
        }));
    } else if (this._table?.id) {
      this.subsList.push(this._tableService.createColumnForTable(this._table.id, column)
        .subscribe(newColumn => {
          if (newColumn) {
            this.columnList.push(newColumn);
            this.selectedColumn = undefined;
          }
        }));
    } else {
      console.error('No Table or Table ID');
    }
  }


  private fetchColumns(table: TableModel | undefined) {
    if (table?.id) {
      this.subsList.push(
        this._tableService.getColumnsForTable(table.id)
          .subscribe(columnList => {
            this.columnList = columnList ?? [];
          })
      );
    } else {
      this.columnList = [];
      this.selectedColumn = undefined;
    }
  }
}
