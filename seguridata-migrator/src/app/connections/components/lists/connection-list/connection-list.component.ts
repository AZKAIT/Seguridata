import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ConnectionModel } from 'src/app/common/models/connection-model';

@Component({
  selector: 'app-connection-list',
  templateUrl: './connection-list.component.html',
  styleUrls: ['./connection-list.component.css']
})
export class ConnectionListComponent {

  @Input() connectionList?: ConnectionModel[];
  @Output() listRefreshEvent = new EventEmitter<void>();
  @Output() editConnEvent = new EventEmitter<void>();
  @Output() deleteConnEvent = new EventEmitter<void>();
  @Output() createConnEvent = new EventEmitter<void>();

  @Input() selectedConn?: ConnectionModel;
  @Output() selectedConnChange = new EventEmitter<ConnectionModel | undefined>();

  refreshList(): void {
    this.listRefreshEvent.next();
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


  onSelectedConnectionFromList(connection: ConnectionModel) {
    this.selectedConn = connection;
    this.selectedConnChange.emit(this.selectedConn);
  }
}
