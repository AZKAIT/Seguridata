import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { BackendFacadeService } from '../facade/backend-facade.service';
import { DefinitionModel } from '../models/definition-model';
import { HttpParams } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class DefinitionService {

  constructor(private backend: BackendFacadeService) { }

  getDefinition(definitionId: string): Observable<DefinitionModel | undefined> {
    const params = new HttpParams();

    return this.backend.serviceGet<DefinitionModel>(`/definitions/${definitionId}`, undefined, params)
    .pipe(map(response => response.body?.data));
  }

  updateDefinition(definitionId: string, newDefData: DefinitionModel): Observable<DefinitionModel | undefined> {
    const params = new HttpParams();

    return this.backend.servicePut<DefinitionModel>(`/definitions/${definitionId}`, newDefData, undefined, params)
    .pipe(map(response => response.body?.data));
  }

  deleteDefinition(definitionId: string): Observable<DefinitionModel | undefined> {
    const params = new HttpParams();

    return this.backend.serviceDelete<DefinitionModel>(`/definitions/${definitionId}`, undefined, params)
    .pipe(map(response => response.body?.data));
  }
}
