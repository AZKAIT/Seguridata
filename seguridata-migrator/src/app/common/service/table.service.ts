import { Injectable } from '@angular/core';
import { BackendFacadeService } from '../facade/backend-facade.service';
import { TableModel } from '../models/table-model';
import { Observable, map } from 'rxjs';
import { HttpParams } from '@angular/common/http';
import { ColumnModel } from '../models/column-model';

@Injectable({
  providedIn: 'root'
})
export class TableService {

  constructor(private backend: BackendFacadeService) { }

  getTable(tableId: string): Observable<TableModel | undefined> {
    const params = new HttpParams();

    return this.backend.serviceGet<TableModel>(`/tables/${tableId}`, undefined, params)
    .pipe(map(response => response.body?.data));
  }

  updateTable(tableId: string, newTableData: TableModel): Observable<TableModel | undefined> {
    const params = new HttpParams();

    return this.backend.servicePut<TableModel>(`/tables/${tableId}`, newTableData, undefined, params)
    .pipe(map(response => response.body?.data));
  }

  deleteTable(tableId: string): Observable<TableModel | undefined> {
    const params = new HttpParams();

    return this.backend.serviceDelete<TableModel>(`/tables/${tableId}`, undefined, params)
    .pipe(map(response => response.body?.data));
  }

  createColumnForTable(tableId: string, column: ColumnModel): Observable<ColumnModel | undefined> {
    const params = new HttpParams();

    return this.backend.servicePost<ColumnModel>(`/tables/${tableId}/columns`, column, undefined, params)
    .pipe(map(response => response.body?.data));
  }

  getColumnsForTable(tableId: string): Observable<ColumnModel[] | undefined> {
    const params = new HttpParams();

    return this.backend.serviceGet<ColumnModel[]>(`/tables/${tableId}/columns`, undefined, params)
    .pipe(map(response => response.body?.data));
  }
}
