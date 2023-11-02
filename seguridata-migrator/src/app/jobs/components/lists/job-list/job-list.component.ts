import { Component, EventEmitter, Input, Output, OnChanges, SimpleChanges } from '@angular/core';
import { TableRowSelectEvent, TableRowUnSelectEvent } from 'primeng/table';
import { JobStatus, parseJobStatusFromValue } from 'src/app/common/enums/job-status';
import { JobModel } from 'src/app/common/models/job-model';

@Component({
  selector: 'app-job-list',
  templateUrl: './job-list.component.html',
  styleUrls: ['./job-list.component.css']
})
export class JobListComponent implements OnChanges {

  @Input() jobList?: JobModel[];
  @Output() listRefreshEvent = new EventEmitter<void>();

  @Input() selectedJob?: JobModel;
  @Output() selectedJobChange = new EventEmitter<JobModel | undefined>();

  @Input() tableLoading?: boolean;

  numTables = 0;

  ngOnChanges(changes: SimpleChanges): void {
    if (this.jobList) {
      this.numTables = this.jobList.length;
    } else {
      this.numTables = 0;
    }
  }


  refreshList() {
    this.listRefreshEvent.emit();
  }

  onRowSelect(event : TableRowSelectEvent) {
    this.selectedJobChange.emit(this.selectedJob);
  }

  onRowUnselect(event : TableRowUnSelectEvent) {
    this.selectedJobChange.emit(this.selectedJob);
  }

  resolveIcon(jobStatus: JobStatus): string {
    const status = parseJobStatusFromValue(jobStatus);

    let icon = 'pi-thumbs-up';
    if (status == JobStatus.STARTING || status == JobStatus.RUNNING || status == JobStatus.STOPPING) {
      icon = 'pi-spin pi-sync';
    } else if (status == JobStatus.FINISHED_SUCCESS) {
      icon = 'pi-check-circle';
    } else if (status == JobStatus.FINISHED_ERROR) {
      icon = 'pi-times-circle';
    } else if (status == JobStatus.FINISHED_WARN) {
      icon = 'pi-exclamation-triangle';
    } else if (status == JobStatus.STOPPED) {
      icon = 'pi-minus-circle';
    }

    return icon;
  }
}
