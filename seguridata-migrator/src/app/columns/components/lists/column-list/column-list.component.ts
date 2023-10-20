import { Component, Input, Output, EventEmitter } from '@angular/core';
import { TableRowSelectEvent, TableRowUnSelectEvent } from 'primeng/table';
import { ColumnModel } from 'src/app/common/models/column-model';
import { TableModel } from 'src/app/common/models/table-model';

@Component({
  selector: 'app-column-list',
  templateUrl: './column-list.component.html',
  styleUrls: ['./column-list.component.css']
})
export class ColumnListComponent {

  @Input() table?: TableModel;
  @Input() columnList?: ColumnModel[];
  @Output() listRefreshEvent = new EventEmitter<void>();
  @Output() editColumnEvent = new EventEmitter<void>();
  @Output() deleteColumnEvent = new EventEmitter<void>();
  @Output() createColumnEvent = new EventEmitter<void>();

  @Input() selectedColumn?: ColumnModel;
  @Output() selectedColumnChange = new EventEmitter<ColumnModel | undefined>();

  @Input() tableLoading?: boolean;
  @Input() deleteLoading?: boolean;


  refreshList(): void {
    this.listRefreshEvent.next();
  }

  editColumn(): void {
    this.editColumnEvent.next();
  }

  deleteColumn(): void {
    this.deleteColumnEvent.next();
  }

  createColumn(): void {
    this.createColumnEvent.next();
  }

  onRowSelect(event : TableRowSelectEvent) {
    this.selectedColumnChange.emit(this.selectedColumn);
  }

  onRowUnselect(event : TableRowUnSelectEvent) {
    this.selectedColumnChange.emit(this.selectedColumn);
  }
}
