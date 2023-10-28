import { Component, OnDestroy, OnInit } from '@angular/core';
import { MessageService } from 'primeng/api';
import { Subscription } from 'rxjs';
import { JobModel } from 'src/app/common/models/job-model';
import { JobService } from 'src/app/common/service/job.service';

@Component({
  selector: 'app-jobs-container',
  templateUrl: './jobs-container.component.html',
  styleUrls: ['./jobs-container.component.css']
})
export class JobsContainerComponent implements OnInit, OnDestroy {
  private _subsList: Subscription[] = [];

  jobList: JobModel[] = [];

  selectedJob: JobModel | undefined;

  tableLoading = false;

  constructor(private _jobService: JobService, private _messageService: MessageService) {
  }

  ngOnInit(): void {
    this.fetchJobs();
  }

  ngOnDestroy(): void {
    let subs: Subscription | undefined;
    while (this._subsList.length) {
      subs = this._subsList.pop();
      if (subs) {
        subs.unsubscribe();
      }
    }
  }



  onExecuteRefresh() {
    this.fetchJobs();
  }


  private fetchJobs() {
    this.tableLoading = true;
    this._subsList.push(this._jobService.getAllJobs()
      .subscribe({
        next: jobList => {
          if (jobList) {
            this.jobList = jobList ?? [];
          }
          this.tableLoading = false;
        },
        error: err => {
          this.postError('Error al cargar Tareas', err?.messages?.join(','));
          this.tableLoading = false;
        }
      }));
  }

  private postError(title: string, message: string) {
    this._messageService.add({ severity: 'error', summary: title, detail: message });
  }
}
