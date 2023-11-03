import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subject, Subscription, takeUntil } from 'rxjs';
import { JobModel } from 'src/app/common/models/job-model';
import { JobService } from 'src/app/jobs/job.service';

@Component({
  selector: 'app-jobs-container',
  templateUrl: './jobs-container.component.html',
  styleUrls: ['./jobs-container.component.css']
})
export class JobsContainerComponent implements OnInit, OnDestroy {
  private destroyed$ = new Subject<void>();

  jobList: JobModel[] = [];
  tableLoading = false;

  selectedJob: JobModel | undefined;

  constructor(private _jobService: JobService) {
  }

  ngOnInit(): void {
    this._jobService.fetchJobs();

    this._jobService.jobListObs()
      .pipe(takeUntil(this.destroyed$))
      .subscribe(jobList => this.jobList = jobList);

    this._jobService.jobsLoadingObs()
    .pipe(takeUntil(this.destroyed$))
    .subscribe(jobsLoading => this.tableLoading = jobsLoading);
  }

  ngOnDestroy(): void {
    this.destroyed$.next();
  }

  onExecuteRefresh() {
    this._jobService.fetchJobs();
  }
}
