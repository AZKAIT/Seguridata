import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { TableRowSelectEvent, TableRowUnSelectEvent } from 'primeng/table';
import { DefinitionModel } from 'src/app/common/models/definition-model';
import { PlanModel } from 'src/app/common/models/plan-model';

@Component({
  selector: 'app-definition-list',
  templateUrl: './definition-list.component.html',
  styleUrls: ['./definition-list.component.css']
})
export class DefinitionListComponent implements OnChanges {

  @Input() plan?: PlanModel;
  @Input() defList?: DefinitionModel[];
  @Output() listRefreshEvent = new EventEmitter<void>();
  @Output() editDefEvent = new EventEmitter<void>();
  @Output() deleteDefEvent = new EventEmitter<void>();
  @Output() createDefEvent = new EventEmitter<void>();

  @Input() selectedDef?: DefinitionModel;
  @Output() selectedDefChange = new EventEmitter<DefinitionModel | undefined>();

  @Input() tableLoading?: boolean;
  @Input() deleteLoading?: boolean;

  firstIndex = 0;
  numTables = 0;

  ngOnChanges(changes: SimpleChanges): void {
    if (this.defList) {
      this.numTables = this.defList.length;
    } else {
      this.numTables = 0;
    }
    this.firstIndex = 0;
  }


  refreshList(): void {
    this.listRefreshEvent.next();
  }

  editDef(): void {
    this.editDefEvent.next();
  }

  deleteDef(): void {
    this.deleteDefEvent.next();
  }

  createDef(): void {
    this.createDefEvent.next();
  }


  onRowSelect(event : TableRowSelectEvent) {
    this.selectedDefChange.emit(this.selectedDef);
  }

  onRowUnselect(event : TableRowUnSelectEvent) {
    this.selectedDefChange.emit(this.selectedDef);
  }
}
