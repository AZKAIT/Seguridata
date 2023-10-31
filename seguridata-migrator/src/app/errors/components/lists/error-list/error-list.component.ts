import { Component, EventEmitter, Input, Output, OnChanges, SimpleChanges } from '@angular/core';
import { TableRowSelectEvent, TableRowUnSelectEvent } from 'primeng/table';
import { ErrorTrackingModel } from 'src/app/common/models/error-tracking-model';
import { JobModel } from 'src/app/common/models/job-model';

@Component({
  selector: 'app-error-list',
  templateUrl: './error-list.component.html',
  styleUrls: ['./error-list.component.css']
})
export class ErrorListComponent implements OnChanges {

  @Input() job?: JobModel;
  @Input() errorList?: ErrorTrackingModel[];
  @Output() listRefreshEvent = new EventEmitter<void>();

  @Input() selectedError?: ErrorTrackingModel;
  @Output() selectedErrorChange = new EventEmitter<ErrorTrackingModel | undefined>();

  @Input() tableLoading?: boolean;

  numTables = 0;

  ngOnChanges(changes: SimpleChanges): void {
    if (this.errorList) {
      this.numTables = this.errorList.length;
    } else {
      this.numTables = 0;
    }
  }


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
