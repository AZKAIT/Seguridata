import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { ConnectionModel } from 'src/app/common/models/connection-model';
import { ConnectionService } from 'src/app/common/service/connection.service';

@Component({
  selector: 'app-connections-container',
  templateUrl: './connections-container.component.html',
  styleUrls: ['./connections-container.component.css']
})
export class ConnectionsContainerComponent implements OnInit, OnDestroy {

  subsList: Subscription[] = [];

  connectionList: ConnectionModel[] = [];
  selectedConn: ConnectionModel | undefined;


  constructor(private _connectionService: ConnectionService) {
  }

  ngOnInit(): void {
    this.fetchConnections();
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

  onExecuteRefresh() {
    this.fetchConnections();
  }

  onEditConnection() {
    console.log(`Execute onEditConnection: ${JSON.stringify(this.selectedConn)}`);
  }

  onDeleteConnection() {
    if (this.selectedConn?.id) {
      this.subsList.push(this._connectionService.deleteConnection(this.selectedConn.id)
        .subscribe(delConn => {
          if (this.selectedConn) {
            this.connectionList.splice(this.connectionList.indexOf(this.selectedConn, 1))
            this.selectedConn = undefined;
          }
        }));
    }
  }

  onCreateConnection() {
    this.selectedConn = undefined;
  }

  saveConnectionData(connection: ConnectionModel) {
    if (connection?.id) {
      this.subsList.push(this._connectionService.updateConnection(connection.id, connection)
        .subscribe(updatedConn => {
          if (this.selectedConn && updatedConn) {
            const prevDataIndex = this.connectionList.indexOf(this.selectedConn);
            this.connectionList[prevDataIndex] = updatedConn;
            this.selectedConn = undefined;
          }
        }));
    } else {
      this.subsList.push(this._connectionService.createConnection(connection)
        .subscribe(newConn => {
          if (newConn) {
            this.connectionList.push(newConn);
            this.selectedConn = undefined;
          }
        }));
    }
  }


  private fetchConnections() {
    this.subsList.push(this._connectionService.getAllConnections()
      .subscribe(connList => {
        this.connectionList = connList ?? [];
      }));
  }
}
