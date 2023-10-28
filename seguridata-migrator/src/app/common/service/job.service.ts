import { Injectable } from '@angular/core';
import { BackendFacadeService } from '../facade/backend-facade.service';
import { JobModel } from '../models/job-model';
import { Observable, map } from 'rxjs';
import { HttpParams } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class JobService {

  constructor(private backend: BackendFacadeService) { }

  getAllJobs(): Observable<JobModel[] | undefined> {
    const params = new HttpParams();

    return this.backend.serviceGet<JobModel[]>(`/jobs`, undefined, params)
    .pipe(map(response => response.body?.data));
  }

  getJobById(jobId: string): Observable<JobModel | undefined> {
    const params = new HttpParams();

    return this.backend.serviceGet<JobModel>(`/jobs/${jobId}`, undefined, params)
    .pipe(map(response => response.body?.data));
  }

  getProjectJobs(projectId: string): Observable<JobModel[] | undefined> {
    const params = new HttpParams();

    return this.backend.serviceGet<JobModel[]>(`/jobs/project/${projectId}`, undefined, params)
    .pipe(map(response => response.body?.data));
  }
}
