import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { TableRowSelectEvent, TableRowUnSelectEvent } from 'primeng/table';
import { PlanModel } from 'src/app/common/models/plan-model';
import { ProjectModel } from 'src/app/common/models/project-model';

@Component({
  selector: 'app-plan-list',
  templateUrl: './plan-list.component.html',
  styleUrls: ['./plan-list.component.css']
})
export class PlanListComponent implements OnChanges {

  @Input() project?: ProjectModel;
  private _planList?: PlanModel[];
  @Output() listRefreshEvent = new EventEmitter<void>();
  @Output() editPlanEvent = new EventEmitter<void>();
  @Output() deletePlanEvent = new EventEmitter<void>();
  @Output() createPlanEvent = new EventEmitter<void>();

  @Input() selectedPlan?: PlanModel;
  @Output() selectedPlanChange = new EventEmitter<PlanModel | undefined>();

  @Input() tableLoading?: boolean;
  @Input() deleteLoading?: boolean;

  firstIndex = 0;
  numTables = 0;

  @Input()
  get planList() {
    return this._planList;
  }
  set planList(planList: PlanModel[] | undefined) {
    this._planList = planList;
    this.firstIndex = 0;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this._planList) {
      this.numTables = this._planList.length;
    } else {
      this.numTables = 0;
    }
  }


  refreshList(): void {
    this.listRefreshEvent.next();
  }

  editPlan(): void {
    this.editPlanEvent.next();
  }

  deletePlan(): void {
    this.deletePlanEvent.next();
  }

  createPlan(): void {
    this.createPlanEvent.next();
  }

  onRowSelect(event : TableRowSelectEvent) {
    this.selectedPlanChange.emit(this.selectedPlan);
  }

  onRowUnselect(event : TableRowUnSelectEvent) {
    this.selectedPlanChange.emit(this.selectedPlan);
  }
}
