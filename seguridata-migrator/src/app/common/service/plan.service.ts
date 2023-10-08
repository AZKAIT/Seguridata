import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { BackendFacadeService } from '../facade/backend-facade.service';
import { PlanModel } from '../models/plan-model';
import { HttpParams } from '@angular/common/http';
import { DefinitionModel } from '../models/definition-model';

@Injectable({
  providedIn: 'root'
})
export class PlanService {

  constructor(private backend: BackendFacadeService) { }

  getPlan(planId: string): Observable<PlanModel | undefined> {
    const params = new HttpParams();
    return this.backend.serviceGet<PlanModel>(`/plans/${planId}`, undefined, params)
    .pipe(map(response => response.body?.data));
  }

  updatePlan(planId: string, newPlanData: PlanModel): Observable<PlanModel | undefined> {
    const params = new HttpParams();
    return this.backend.servicePut<PlanModel>(`/plans/${planId}`, newPlanData, undefined, params)
    .pipe(map(response => response.body?.data));
  }

  deletePlan(planId: string): Observable<PlanModel | undefined> {
    const params = new HttpParams();

    return this.backend.serviceDelete<PlanModel>(`/plans/${planId}`, undefined, params)
    .pipe(map(response => response.body?.data));
  }

  createDefinitionForPlan(planId: string, definition: DefinitionModel): Observable<DefinitionModel | undefined> {
    const params = new HttpParams();

    return this.backend.servicePost<DefinitionModel>(`/plans/${planId}/definitions`, definition, undefined, params)
    .pipe(map(response => response.body?.data));
  }

  getDefinitionsForPlan(planId: string): Observable<DefinitionModel[] | undefined> {
    const params = new HttpParams();

    return this.backend.serviceGet<DefinitionModel[]>(`/plans/${planId}/definitions`, undefined, params)
    .pipe(map(response => response.body?.data));
  }
}
