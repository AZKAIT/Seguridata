import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { TableRowSelectEvent, TableRowUnSelectEvent } from 'primeng/table';
import { ConnectionModel } from 'src/app/common/models/connection-model';
import { TableModel } from 'src/app/common/models/table-model';

@Component({
  selector: 'app-table-list',
  templateUrl: './table-list.component.html',
  styleUrls: ['./table-list.component.css']
})
export class TableListComponent implements OnChanges {

  @Input() connection?: ConnectionModel;
  @Input() tableList?: TableModel[];
  @Output() listRefreshEvent = new EventEmitter<void>();
  @Output() editTableEvent = new EventEmitter<void>();
  @Output() deleteTableEvent = new EventEmitter<void>();
  @Output() createTableEvent = new EventEmitter<void>();

  numTables = 0;

  @Input() selectedTable?: TableModel;
  @Output() selectedTableChange = new EventEmitter<TableModel | undefined>();

  @Input() tableLoading?: boolean;
  @Input() deleteLoading?: boolean;

  ngOnChanges(changes: SimpleChanges): void {
    if (this.tableList) {
      this.numTables = this.tableList.length;
    } else {
      this.numTables = 0;
    }
  }


  refreshList(): void {
    this.listRefreshEvent.next();
  }

  editTable(): void {
    this.editTableEvent.next();
  }

  deleteTable(): void {
    this.deleteTableEvent.next();
  }

  createTable(): void {
    this.createTableEvent.next();
  }

  onRowSelect(event : TableRowSelectEvent) {
    this.selectedTableChange.emit(this.selectedTable);
  }

  onRowUnselect(event : TableRowUnSelectEvent) {
    this.selectedTableChange.emit(this.selectedTable);
  }
}
