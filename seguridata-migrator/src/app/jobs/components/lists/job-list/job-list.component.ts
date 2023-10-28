import { Component, EventEmitter, Input, Output } from '@angular/core';
import { TableRowSelectEvent, TableRowUnSelectEvent } from 'primeng/table';
import { JobModel } from 'src/app/common/models/job-model';

@Component({
  selector: 'app-job-list',
  templateUrl: './job-list.component.html',
  styleUrls: ['./job-list.component.css']
})
export class JobListComponent {

  @Input() jobList?: JobModel[];
  @Output() listRefreshEvent = new EventEmitter<void>();

  @Input() selectedJob?: JobModel;
  @Output() selectedJobChange = new EventEmitter<JobModel | undefined>();

  @Input() tableLoading?: boolean;


  refreshList() {
    this.listRefreshEvent.emit();
  }

  onRowSelect(event : TableRowSelectEvent) {
    this.selectedJobChange.emit(this.selectedJob);
  }

  onRowUnselect(event : TableRowUnSelectEvent) {
    this.selectedJobChange.emit(this.selectedJob);
  }
}
