import { Injectable } from '@angular/core';
import { JobClientService } from '../common/client/job-client.service';
import { MessageService } from 'primeng/api';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { JobModel } from '../common/models/job-model';
import { ExecutionStatisticsModel } from '../common/models/execution-statistics-model';

@Injectable({
  providedIn: 'root'
})
export class JobService {

  private jobsLoading$ = new BehaviorSubject<boolean>(false);
  private jobList$ = new BehaviorSubject<JobModel[]>([]);

  private _jobList: JobModel[] = [];

  private subs!: Subscription;

  constructor(private _jobClientService: JobClientService, private _messageService: MessageService) { }

  fetchJobs() {
    if (this.subs?.closed == false) {
      this.subs.unsubscribe();
    }

    this.jobsLoading$.next(true);
    this.subs = this._jobClientService.getAllJobs()
      .subscribe({
        next: jobList => {
          if (jobList) {
            this._jobList = jobList ?? [];
            this.jobList$.next(this._jobList);
          }
          this.jobsLoading$.next(false);
        },
        error: err => {
          this.postError('Error al cargar Tareas', err?.messages?.join(','));
          this.jobsLoading$.next(false);
        }
      });
  }

  jobsLoadingObs(): Observable<boolean> {
    return this.jobsLoading$.asObservable();
  }

  jobListObs(): Observable<JobModel[]> {
    return this.jobList$.asObservable();
  }

  updateJobExecStat(jobId: string, stats: ExecutionStatisticsModel) {
    const jobIndex = this._jobList.findIndex(job => job.id == jobId);
    const job = this._jobList[jobIndex];

    const planIndex = job.planStats.findIndex(planStats => planStats.planId == stats.planId);


    if (stats?.progress) {
      job.planStats[planIndex].progress = stats.progress;
    }

    if (stats?.result) {
      job.planStats[planIndex].result = stats.result;
    }

    if (stats?.status) {
      job.planStats[planIndex].status = stats.status;
    }

    this._jobList[jobIndex] = job;
  }



  private postError(title: string, message: string) {
    this._messageService.add({ severity: 'error', summary: title, detail: message });
  }
}
