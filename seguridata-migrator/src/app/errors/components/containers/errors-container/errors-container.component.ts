import { Component, Input, OnDestroy } from '@angular/core';
import { MessageService } from 'primeng/api';
import { Subscription } from 'rxjs';
import { ErrorTrackingModel } from 'src/app/common/models/error-tracking-model';

import { JobModel } from 'src/app/common/models/job-model';
import { ErrorTrackingService } from 'src/app/common/service/error-tracking.service';

@Component({
  selector: 'app-errors-container',
  templateUrl: './errors-container.component.html',
  styleUrls: ['./errors-container.component.css']
})
export class ErrorsContainerComponent implements OnDestroy {

  private _subsList: Subscription[] = [];

  _job?: JobModel;

  errorList: ErrorTrackingModel[] = [];

  selectedError?: ErrorTrackingModel;
  tableLoading = false;

  constructor(private _errorService: ErrorTrackingService, private _messageService: MessageService) {
  }

  @Input()
  get job(): JobModel | undefined { return this._job; }
  set job(project: JobModel | undefined) {
    this._job = project;
    this.fetchErrors(this._job);
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
    this.fetchErrors(this._job);
  }

  private fetchErrors(job: JobModel | undefined) {
    this.selectedError = undefined;
    this.tableLoading = true;
    if (job?.id) {
      this._subsList.push(
        this._errorService.getJobErrors(job.id)
          .subscribe({
            next: errorList => {
              this.errorList = errorList ?? [];
              this.tableLoading = false;
            },
            error: err => {
              this.tableLoading = false;
              this.postError('Error al cargar Planes', err?.messages?.join(','));
            }
          })
      );
    } else {
      this.errorList = [];
      this.selectedError = undefined;
      this.tableLoading = false;
    }
  }

  private postError(title: string, message: string) {
    this._messageService.add({ severity: 'error', summary: title, detail: message });
  }
}
