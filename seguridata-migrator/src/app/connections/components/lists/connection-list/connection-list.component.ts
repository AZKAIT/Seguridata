import { Component, EventEmitter, Input, Output, OnChanges, SimpleChanges } from '@angular/core';
import { TableRowSelectEvent, TableRowUnSelectEvent } from 'primeng/table';
import { ConnectionModel } from 'src/app/common/models/connection-model';

@Component({
  selector: 'app-connection-list',
  templateUrl: './connection-list.component.html',
  styleUrls: ['./connection-list.component.css']
})
export class ConnectionListComponent implements OnChanges {

  @Input() connectionList?: ConnectionModel[];
  @Output() listRefreshEvent = new EventEmitter<void>();
  @Output() syncUpEvent = new EventEmitter<void>();
  @Output() editConnEvent = new EventEmitter<void>();
  @Output() deleteConnEvent = new EventEmitter<void>();
  @Output() createConnEvent = new EventEmitter<void>();

  @Input() selectedConn?: ConnectionModel;
  @Output() selectedConnChange = new EventEmitter<ConnectionModel | undefined>();

  @Input() tableLoading?: boolean;
  @Input() deleteLoading?: boolean;
  @Input() syncUpLoading?: boolean;

  firstIndex = 0;
  numTables = 0;

  ngOnChanges(changes: SimpleChanges): void {
    if (this.connectionList) {
      this.numTables = this.connectionList.length;
    } else {
      this.numTables = 0;
    }
    this.firstIndex = 0;
  }

  refreshList(): void {
    this.listRefreshEvent.next();
  }

  syncUpConnection(): void {
    this.syncUpEvent.next();
  }

  editConnection(): void {
    this.editConnEvent.next();
  }

  deleteConnection(): void {
    this.deleteConnEvent.next();
  }

  createConnection(): void {
    this.createConnEvent.next();
  }


  onRowSelect(event : TableRowSelectEvent) {
    this.selectedConnChange.emit(this.selectedConn);
  }

  onRowUnselect(event : TableRowUnSelectEvent) {
    this.selectedConnChange.emit(this.selectedConn);
  }
}
