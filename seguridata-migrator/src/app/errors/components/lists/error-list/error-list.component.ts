import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
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
  private _errorList?: ErrorTrackingModel[];
  @Output() listRefreshEvent = new EventEmitter<void>();

  @Input() selectedError?: ErrorTrackingModel;
  @Output() selectedErrorChange = new EventEmitter<ErrorTrackingModel | undefined>();

  @Input() tableLoading?: boolean;

  firstIndex = 0;
  numTables = 0;

  @Input()
  get errorList() {
    return this._errorList;
  }
  set errorList(errorList: ErrorTrackingModel[] | undefined) {
    this._errorList = errorList;
    this.firstIndex = 0;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this._errorList) {
      this.numTables = this._errorList.length;
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
