import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { TableRowSelectEvent, TableRowUnSelectEvent } from 'primeng/table';
import { ColumnModel } from 'src/app/common/models/column-model';
import { TableModel } from 'src/app/common/models/table-model';

@Component({
  selector: 'app-column-list',
  templateUrl: './column-list.component.html',
  styleUrls: ['./column-list.component.css']
})
export class ColumnListComponent implements OnChanges {

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

  numTables = 0;

  ngOnChanges(changes: SimpleChanges): void {
    if (this.columnList) {
      this.numTables = this.columnList.length;
    } else {
      this.numTables = 0;
    }
  }


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
