import { Injectable } from '@angular/core';
import { BackendFacadeService } from '../facade/backend-facade.service';
import { Observable, map } from 'rxjs';
import { ConnectionModel } from '../models/connection-model';
import { HttpParams } from '@angular/common/http';
import { ResponseWrapperModel } from '../models/response-wrapper-model';
import { TableModel } from '../models/table-model';

@Injectable({
  providedIn: 'root'
})
export class ConnectionService {

  constructor(private backend: BackendFacadeService) { }


  createConnection(connection: ConnectionModel): Observable<ConnectionModel | undefined> {
    const params = new HttpParams();
    return this.backend.servicePost<ConnectionModel>(`/connections`, connection, undefined, params)
    .pipe(map(response => response.body?.data));
  }

  getAllConnections(): Observable<ConnectionModel[] | undefined> {
    const params = new HttpParams();

    return this.backend.serviceGet<ConnectionModel[]>(`/connections`, undefined, params)
    .pipe(map(response => response.body?.data)); // TODO: Validate response before returning
  }

  getConnection(connectionId: string): Observable<ConnectionModel | undefined> {
    const params = new HttpParams();

    return this.backend.serviceGet<ConnectionModel>(`/connections/${connectionId}`, undefined, params)
    .pipe(map(response => response.body?.data));
  }

  updateConnection(connectionId: string, newConnData: ConnectionModel): Observable<ConnectionModel | undefined> {
    const params = new HttpParams();

    return this.backend.servicePut<ConnectionModel>(`/connections/${connectionId}`, newConnData, undefined, params)
    .pipe(map(response => response.body?.data));
  }

  deleteConnection(connectionId: string): Observable<ConnectionModel | undefined> {
    const params = new HttpParams();

    return this.backend.serviceDelete<ConnectionModel>(`/connections/${connectionId}`, undefined, params)
    .pipe(map(response => response.body?.data));
  }

  createTableForConnection(connectionId: string, table: TableModel): Observable<TableModel | undefined> {
    const params = new HttpParams();

    return this.backend.servicePost<TableModel>(`/connections/${connectionId}/tables`, table, undefined, params)
    .pipe(map(response => response.body?.data));
  }

  getTablesForConnection(connectionId: string): Observable<TableModel[] | undefined> {
    const params = new HttpParams();

    return this.backend.serviceGet<TableModel[]>(`/connections/${connectionId}/tables`, undefined, params)
    .pipe(map(response => response.body?.data));
  }

  syncUpTables(connectionId: string): Observable<any> {
    const params = new HttpParams();

    return this.backend.servicePost<TableModel[]>(`/connections/${connectionId}/sync/tables`, undefined, undefined, params)
    .pipe(map(response => response.body?.data));
  }
}
