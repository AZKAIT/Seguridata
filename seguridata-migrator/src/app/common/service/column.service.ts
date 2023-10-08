import { Injectable } from '@angular/core';
import { BackendFacadeService } from '../facade/backend-facade.service';
import { ColumnModel } from '../models/column-model';
import { Observable, map } from 'rxjs';
import { HttpParams } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ColumnService {

  constructor(private backend: BackendFacadeService) { }

  getColumn(columnId: string): Observable<ColumnModel | undefined> {
    const params = new HttpParams();

    return this.backend.serviceGet<ColumnModel>(`/columns/${columnId}`, undefined, params)
    .pipe(map(response => response.body?.data));
  }

  updateColumn(columnId: string, newColData: ColumnModel): Observable<ColumnModel | undefined> {
    const params = new HttpParams();

    return this.backend.servicePut<ColumnModel>(`/columns/${columnId}`, newColData, undefined, params)
    .pipe(map(response => response.body?.data));
  }

  deleteColumn(columnId: string): Observable<ColumnModel | undefined> {
    const params = new HttpParams();

    return this.backend.serviceDelete<ColumnModel>(`/columns/${columnId}`, undefined, params)
    .pipe(map(response => response.body?.data));
  }
}
