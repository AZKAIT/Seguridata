import { Injectable } from '@angular/core';
import { BackendFacadeService } from '../facade/backend-facade.service';
import { Observable, map } from 'rxjs';
import { SeguriDataConfigPropsModel } from '../models/seguri-data-config-props-model';
import { HttpParams } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class PropertiesClientService {

  constructor(private backend: BackendFacadeService) { }

  getProperties(): Observable<SeguriDataConfigPropsModel | undefined> {
    const params = new HttpParams();

    return this.backend.serviceGet<SeguriDataConfigPropsModel>(`/properties`, undefined, params)
    .pipe(map(response => response.body?.data));
  }
}
