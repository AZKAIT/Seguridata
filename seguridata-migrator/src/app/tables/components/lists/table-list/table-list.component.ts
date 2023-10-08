import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ConnectionModel } from 'src/app/common/models/connection-model';
import { TableModel } from 'src/app/common/models/table-model';

@Component({
  selector: 'app-table-list',
  templateUrl: './table-list.component.html',
  styleUrls: ['./table-list.component.css']
})
export class TableListComponent {

  @Input() connection?: ConnectionModel;
  @Input() tableList?: TableModel[];
  @Output() listRefreshEvent = new EventEmitter<void>();
  @Output() editTableEvent = new EventEmitter<void>();
  @Output() deleteTableEvent = new EventEmitter<void>();
  @Output() createTableEvent = new EventEmitter<void>();

  @Input() selectedTable?: TableModel;
  @Output() selectedTableChange = new EventEmitter<TableModel | undefined>();


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

  onSelectedTableFromList(table: TableModel) {
    this.selectedTable = table;
    this.selectedTableChange.emit(this.selectedTable);
  }
}
