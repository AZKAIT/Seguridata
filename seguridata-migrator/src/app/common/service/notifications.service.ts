import { Inject, Injectable } from '@angular/core';
import { DOCUMENT } from '@angular/common';
import { RxStomp, RxStompConfig } from '@stomp/rx-stomp';
import { Message } from '@stomp/stompjs';
import { MessageService } from 'primeng/api';

import { webSocket, WebSocketSubject } from 'rxjs/webSocket';
import { Observable, of, filter, map, switchMap, retry, delay, Subject, takeUntil } from 'rxjs';
import { NotificationModel } from '../models/notification-model';
import { JobStatus, parseJobStatusFromValue } from 'src/app/common/enums/job-status';


const RETRY_SECONDS = 10;

@Injectable({
  providedIn: 'root'
})
export class NotificationsService {

  private _stompConfig: RxStompConfig;
  private _destroyed = new Subject<void>();
  private _stompClient = new RxStomp();

  private _url: string;

  private _connection!: WebSocketSubject<any>;


  constructor(@Inject(DOCUMENT) private document: any, private _messageService: MessageService) {
    const loc: string = this.document.location.toString();
    const uiIndex = loc.indexOf("/ui");

    if (uiIndex == -1) {
      this._url = 'http://localhost:8080/migration';
    } else {
      this._url = loc.substring(0, uiIndex);
    }

    this._stompConfig = {
      brokerURL: this._url.replace(/^http/, 'ws') + '/notifications',
      //webSocketFactory: function () {
      //  return new SockJS('ws://localhost:8080/migration/notifications');
      //},
      connectHeaders: {
        login: 'guest',
        passcode: 'guest',
      },

      heartbeatIncoming: 0, // Typical value 0 - disabled
      heartbeatOutgoing: 20000, // Typical value 20000 - every 20 seconds
      reconnectDelay: 5000,

      // Will log diagnostics on console
      // It can be quite verbose, not recommended in production
      // Skip this key to stop logging to console
      //debug: (msg: string): void => {
      //  console.log(new Date(), msg);
      //}
    }

    this._stompClient.configure(this._stompConfig);
    this._stompClient.activate();
  }

  connectToNotifs(): void {
    this._stompClient.watch('/topic/job/execution/status')
      .pipe(takeUntil(this._destroyed))
      .subscribe(this.postProjExecStatusNotif.bind(this));

    this._stompClient.watch('/topic/job/execution/error')
      .pipe(takeUntil(this._destroyed))
      .subscribe(this.postProjExecErrorNotif.bind(this));

    this._stompClient.watch('/topic/project/syncup/status')
      .pipe(takeUntil(this._destroyed))
      .subscribe(this.postProjSyncUpStatusNotif.bind(this));

    this._stompClient.watch('/topic/project/syncup/error')
      .pipe(takeUntil(this._destroyed))
      .subscribe(this.postProjSyncUpErrorNotif.bind(this));

    this._stompClient.watch('/topic/connection/syncup/status')
      .pipe(takeUntil(this._destroyed))
      .subscribe(this.postConnSyncUpStatusNotif.bind(this));

    this._stompClient.watch('/topic/connection/syncup/error')
      .pipe(takeUntil(this._destroyed))
      .subscribe(this.postConnSyncUpErrorNotif.bind(this));
  }

  closeConnection(): void {
    this._destroyed.next();
  }

  private postProjExecStatusNotif(message: Message) {
    const statusChange: NotificationModel = JSON.parse(message.body) as NotificationModel;

    this._messageService.add({
      severity: 'info',
      summary: `Tarea: ${statusChange.objectName}`,
      detail: `Tarea cambió de estatus a ${this.translateJobStatus(statusChange.data)}`
    });
  }

  private postProjExecErrorNotif(message: Message) {
    const statusChange: NotificationModel = JSON.parse(message.body) as NotificationModel;

    this._messageService.add({
      severity: 'error',
      summary: `Error en Tarea: ${statusChange.objectName}`,
      detail: `Ocurrió un error al ejecutar la Tarea: ${statusChange.data}`
    });
  }

  private postProjSyncUpStatusNotif(message: Message) {
    const statusChange: NotificationModel = JSON.parse(message.body) as NotificationModel;

    this._messageService.add({
      severity: 'info',
      summary: `Sincronización de Proyecto: ${statusChange.objectName}`,
      detail: `El estatus de la sincronización del Proyecto es: ${statusChange.data}`
    });
  }

  private postProjSyncUpErrorNotif(message: Message) {
    const statusChange: NotificationModel = JSON.parse(message.body) as NotificationModel;

    this._messageService.add({
      severity: 'error',
      summary: `Sincronización de Proyecto: ${statusChange.objectName}`,
      detail: `Error al sincronizar: ${statusChange.data}`
    });
  }

  private postConnSyncUpStatusNotif(message: Message) {
    const statusChange: NotificationModel = JSON.parse(message.body) as NotificationModel;

    this._messageService.add({
      severity: 'info',
      summary: `Sincronización de Conexión: ${statusChange.objectName}`,
      detail: `El estatus de la sincronización de la Conexión es: ${statusChange.data}`
    });
  }

  private postConnSyncUpErrorNotif(message: Message) {
    const statusChange: NotificationModel = JSON.parse(message.body) as NotificationModel;

    this._messageService.add({
      severity: 'error',
      summary: `Error en sincronización de Conexión: ${statusChange.objectName}`,
      detail: `Ocurrió un error al sincronizar la Conexión: ${statusChange.data}`
    });
  }

  private translateJobStatus(js: string): string {
    const status: JobStatus | undefined = parseJobStatusFromValue(js);
    if (!status) {
      return '';
    }

    switch(status) {
      case JobStatus.STARTING: return 'INICIANDO';
       case JobStatus.RUNNING: return 'EJECUTANDO';
       case JobStatus.STOPPING: return 'DETENIENDO';
       case JobStatus.STOPPED: return 'DETENIDO';
       case JobStatus.FINISHED_SUCCESS: return 'TERMINADO_EXITOSAMENTE';
       case JobStatus.FINISHED_WARN: return 'TERMINADO_ADVERTENCIA';
       case JobStatus.FINISHED_ERROR: return 'TERMINADO_ERROR';
    }
  }



  /**
   * @deprecated Kept only as a reference, this method should not be used
   * @returns Observable of WebSocker
   */
  private createConnection(): Observable<any> {
    const websocketUrl = this._url.replace(/^http/, 'ws') + '/notifications';

    return of(websocketUrl).pipe(
      filter(apiUrl => !!apiUrl),
      // https becomes wws, http becomes ws
      map(apiUrl => apiUrl.replace(/^http/, 'ws') + '/stream'),
      switchMap(wsUrl => {
        if (this._connection) {
          return this._connection;
        } else {
          this._connection = webSocket(wsUrl);
          return this._connection;
        }
      }),
      retry({ delay: (errors, retryCount) => errors.pipe(delay(RETRY_SECONDS)) })
    );
  }
}
