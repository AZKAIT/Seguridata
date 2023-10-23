import { Injectable } from '@angular/core';
import { BackendFacadeService } from '../facade/backend-facade.service';
import { Observable, map } from 'rxjs';
import { HttpParams } from '@angular/common/http';
import { DashboardDataModel } from '../models/dashboard-data-model';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  constructor(private backend: BackendFacadeService) { }

  getDashboardData(): Observable<DashboardDataModel | undefined> {
    const params = new HttpParams();

    return this.backend.serviceGet<DashboardDataModel>(`/dashboard`, undefined, params)
    .pipe(map(response => response.body?.data));
  }
}
