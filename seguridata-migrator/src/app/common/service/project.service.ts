import { Injectable } from '@angular/core';
import { BackendFacadeService } from '../facade/backend-facade.service';
import { ProjectModel } from '../models/project-model';
import { Observable, map } from 'rxjs';
import { HttpParams } from '@angular/common/http';
import { PlanModel } from '../models/plan-model';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {

  constructor(private backend: BackendFacadeService) { }

  createProject(project: ProjectModel): Observable<ProjectModel | undefined> {
    const params = new HttpParams();
    return this.backend.servicePost<ProjectModel>(`/projects`, project, undefined, params)
    .pipe(map(response => response.body?.data));
  }

  getAllProjects(): Observable<ProjectModel[] | undefined> {
    const params = new HttpParams();
    return this.backend.serviceGet<ProjectModel[]>(`/projects`, undefined, params)
    .pipe(map(response => response.body?.data));
  }

  getProject(projectId: string): Observable<ProjectModel | undefined> {
    const params = new HttpParams();
    return this.backend.serviceGet<ProjectModel>(`/projects/${projectId}`, undefined, params)
    .pipe(map(response => response.body?.data));
  }

  updateProject(projectId: string, newProjData: ProjectModel): Observable<ProjectModel | undefined> {
    const params = new HttpParams();
    return this.backend.servicePut<ProjectModel>(`/projects/${projectId}`, newProjData, undefined, params)
    .pipe(map(response => response.body?.data));
  }

  deleteProject(projectId: string): Observable<ProjectModel | undefined> {
    const params = new HttpParams();
    return this.backend.serviceDelete<ProjectModel>(`/projects/${projectId}`, undefined, params)
    .pipe(map(response => response.body?.data));
  }

  getPlansForProject(projectId: string): Observable<PlanModel[] | undefined> {
    const params = new HttpParams();
    return this.backend.serviceGet<PlanModel[]>(`/projects/${projectId}/plans`, undefined, params)
    .pipe(map(response => response.body?.data));
  }

  createPlanForProject(projectId: string, plan: PlanModel): Observable<PlanModel | undefined> {
    const params = new HttpParams();
    return this.backend.servicePost<PlanModel>(`/projects/${projectId}/plans`, plan, undefined, params)
    .pipe(map(response => response.body?.data));
  }

  startProject(projectId: string): Observable<boolean | undefined> {
    const params = new HttpParams();
    return this.backend.servicePost<boolean>(`/projects/${projectId}/start`, undefined, undefined, params)
    .pipe(map(response => response.body?.data));
  }

  stopProject(projectId: string): Observable<boolean | undefined> {
    const params = new HttpParams();
    return this.backend.servicePost<boolean>(`/projects/${projectId}/stop`, undefined, undefined, params)
    .pipe(map(response => response.body?.data));
  }
}
