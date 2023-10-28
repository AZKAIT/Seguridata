import { Component, EventEmitter, Input, Output } from '@angular/core';
import { TableRowSelectEvent, TableRowUnSelectEvent } from 'primeng/table';
import { ErrorTrackingModel } from 'src/app/common/models/error-tracking-model';
import { JobModel } from 'src/app/common/models/job-model';

@Component({
  selector: 'app-error-list',
  templateUrl: './error-list.component.html',
  styleUrls: ['./error-list.component.css']
})
export class ErrorListComponent {

  @Input() job?: JobModel;
  @Input() errorList?: ErrorTrackingModel[];
  @Output() listRefreshEvent = new EventEmitter<void>();

  @Input() selectedError?: ErrorTrackingModel;
  @Output() selectedErrorChange = new EventEmitter<ErrorTrackingModel | undefined>();

  @Input() tableLoading?: boolean;


  refreshList() {
    this.listRefreshEvent.emit();
  }

  onRowSelect(event : TableRowSelectEvent) {
    this.selectedErrorChange.emit(this.selectedError);
  }

  onRowUnselect(event : TableRowUnSelectEvent) {
    this.selectedErrorChange.emit(this.selectedError);
  }
}
