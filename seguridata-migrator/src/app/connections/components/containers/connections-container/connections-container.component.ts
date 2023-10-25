import { Component, OnDestroy, OnInit } from '@angular/core';
import { MessageService } from 'primeng/api';
import { Subscription } from 'rxjs';
import { ConnectionModel } from 'src/app/common/models/connection-model';
import { ConnectionService } from 'src/app/common/service/connection.service';

@Component({
  selector: 'app-connections-container',
  templateUrl: './connections-container.component.html',
  styleUrls: ['./connections-container.component.css']
})
export class ConnectionsContainerComponent implements OnInit, OnDestroy {

  private _subsList: Subscription[] = [];

  connectionList: ConnectionModel[] = [];
  selectedConn: ConnectionModel | undefined;

  showForm: boolean = false;

  tableLoading: boolean = false;
  formLoading: boolean = false;
  deleteLoading: boolean = false;
  syncLoading = false;


  constructor(private _connectionService: ConnectionService, private _messageService: MessageService) {
  }

  ngOnInit(): void {
    this.fetchConnections();
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

  onExecuteRefresh() {
    this.fetchConnections();
  }

  onEditConnection() {
    this.showForm = true;
  }

  onSyncUpEvent() {
    this.syncLoading = true;
    if (this.selectedConn?.id) {
      this._subsList.push(this._connectionService.syncUpTables(this.selectedConn.id)
      .subscribe({
        next: result => {
          if (result) {
            this.selectedConn = undefined;
            this.postSuccess('Conexión Sincronizada', `La Conexión se ha sincronizado`);
          }
          this.syncLoading = false;
        },
        error: err => {
          this.postError('Error al sincronizar Conexión', err?.messages?.join(','));
          this.syncLoading = false;
        }
      }));
    }
  }

  onDeleteConnection() {
    this.deleteLoading = true;
    if (this.selectedConn?.id) {
      this._subsList.push(this._connectionService.deleteConnection(this.selectedConn.id)
        .subscribe({
          next: delConn => {
            if (this.selectedConn) {
              this.connectionList.splice(this.connectionList.indexOf(this.selectedConn), 1);
              this.selectedConn = undefined;
              this.postSuccess('Eliminar Conexión', `Conexión ${delConn?.name} eliminada`);
            }
            this.deleteLoading = false;
          },
          error: err => {
            this.postError('Error al eliminar Conexión', err?.messages?.join(','));
            this.deleteLoading = false;
          }
        }));
    }
  }

  onCreateConnection() {
    this.selectedConn = undefined;
    this.showForm = true;
  }

  saveConnectionData(connection: ConnectionModel) {
    this.formLoading = true;
    if (connection?.id) {
      this._subsList.push(this._connectionService.updateConnection(connection.id, connection)
        .subscribe({
          next: updatedConn => {
            if (this.selectedConn && updatedConn) {
              const prevDataIndex = this.connectionList.indexOf(this.selectedConn);
              this.connectionList[prevDataIndex] = updatedConn;
              this.selectedConn = undefined;
              this.showForm = false;
              this.postSuccess('Actualizar Conexión', `Conexión ${updatedConn?.name} actualizada`);
            }
            this.formLoading = false;
          },
          error: err => {
            this.postError('Error al actualizar Conexión', err?.messages?.join(','));
            this.formLoading = false;
          }
        }));
    } else {
      this._subsList.push(this._connectionService.createConnection(connection)
        .subscribe({
          next: newConn => {
            if (newConn) {
              this.connectionList.push(newConn);
              this.selectedConn = undefined;
              this.showForm = false;
              this.postSuccess('Crear Conexión', `Conexión ${newConn?.name} creada`);
            }
            this.formLoading = false;
          },
          error: err => {
            this.postError('Error al crear Conexión', err?.messages?.join(','));
            this.formLoading = false;
          }
        }));
    }
  }


  private fetchConnections() {
    this.selectedConn = undefined;
    this.tableLoading = true;
    this._subsList.push(this._connectionService.getAllConnections()
      .subscribe({
        next: connList => {
          this.connectionList = connList ?? [];
          this.tableLoading = false;
        },
        error: err => {
          this.postError('Error al cargar Conexiones', err?.messages?.join(','));
          this.tableLoading = false;
        }
      }));
  }

  private postError(title: string, message: string) {
    this._messageService.add({ severity: 'error', summary: title, detail: message });
  }

  private postSuccess(title: string, message: string) {
    this._messageService.add({ severity: 'success', summary: title, detail: message });
  }
}
