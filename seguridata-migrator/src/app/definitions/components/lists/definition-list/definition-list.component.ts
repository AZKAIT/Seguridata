import { Component, Input, Output, EventEmitter } from '@angular/core';
import { DefinitionModel } from 'src/app/common/models/definition-model';
import { PlanModel } from 'src/app/common/models/plan-model';

@Component({
  selector: 'app-definition-list',
  templateUrl: './definition-list.component.html',
  styleUrls: ['./definition-list.component.css']
})
export class DefinitionListComponent {

  @Input() plan?: PlanModel;
  @Input() defList?: DefinitionModel[];
  @Output() listRefreshEvent = new EventEmitter<void>();
  @Output() editDefEvent = new EventEmitter<void>();
  @Output() deleteDefEvent = new EventEmitter<void>();
  @Output() createDefEvent = new EventEmitter<void>();

  @Input() selectedDef?: DefinitionModel;
  @Output() selectedDefChange = new EventEmitter<DefinitionModel | undefined>();


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

  onSelectedDefFromList(def: DefinitionModel) {
    this.selectedDef = def;
    this.selectedDefChange.emit(this.selectedDef);
  }
}
