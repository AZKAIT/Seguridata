import { Component, EventEmitter, OnDestroy } from '@angular/core';
import { WizardFormWrapper } from '../wizard-form-wrapper';
import { ConnectionModel } from 'src/app/common/models/connection-model';
import { Observable, Subscription } from 'rxjs';
import { ConnectionService } from 'src/app/common/service/connection.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-connection-form-wrapper',
  templateUrl: './connection-form-wrapper.component.html',
  styleUrls: ['./connection-form-wrapper.component.css']
})
export class ConnectionFormWrapperComponent implements WizardFormWrapper<ConnectionModel>, OnDestroy {
  private _index: number = -2;
  private _name!: string;
  private _indexEmitter = new EventEmitter<number>();
  private _resultEmitter = new EventEmitter<ConnectionModel>();

  private _subsList: Subscription[] = [];
  ongoingConn!: ConnectionModel;

  formLoading: boolean = false;

  constructor(private _connService: ConnectionService, private _messageService: MessageService) {
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

  setIndex(index: number): void {
    this._index = index;
  }

  getIndexEmitter(): Observable<number> {
    return this._indexEmitter.asObservable();
  }

  emitNextIndex(): void {
    this._indexEmitter.next(this._index + 1);
  }

  emitPreviousIndex(): void {
    this._indexEmitter.next(this._index - 1);
  }

  getName(): string {
      return this._name;
  }

  setName(name: string): void {
      this._name = name;
  }

  getResult(): Observable<ConnectionModel> {
    return this._resultEmitter.asObservable();
  }

  saveConnectionData(connection: ConnectionModel) {
    this.formLoading = true;
    if (connection?.id) {
      this._subsList.push(this._connService.updateConnection(connection.id, connection)
        .subscribe({
          next: updatedConn => {
            if (this.ongoingConn && updatedConn) {
              this.ongoingConn = updatedConn;
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
      this._subsList.push(this._connService.createConnection(connection)
        .subscribe({
          next: newConn => {
            if (newConn) {
              this.ongoingConn = newConn;
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

  private postError(title: string, message: string) {
    this._messageService.add({ severity: 'error', summary: title, detail: message });
  }

  private postSuccess(title: string, message: string) {
    this._messageService.add({ severity: 'success', summary: title, detail: message });
  }
}
