import { Injectable } from '@angular/core';
import { BackendFacadeService } from '../facade/backend-facade.service';
import { JobModel } from '../models/job-model';
import { Observable, map } from 'rxjs';
import { HttpParams } from '@angular/common/http';
import { ErrorTrackingModel } from '../models/error-tracking-model';

@Injectable({
  providedIn: 'root'
})
export class ErrorTrackingService {

  constructor(private backend: BackendFacadeService) { }

  getJobErrors(jobId: string): Observable<ErrorTrackingModel[] | undefined> {
    const params = new HttpParams();

    return this.backend.serviceGet<ErrorTrackingModel[]>(`/jobs/${jobId}/errors`, undefined, params)
    .pipe(map(response => response.body?.data));
  }
}
