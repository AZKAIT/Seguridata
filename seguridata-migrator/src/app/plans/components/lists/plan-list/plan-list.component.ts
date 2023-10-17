import { Component, Input, Output, EventEmitter } from '@angular/core';
import { PlanModel } from 'src/app/common/models/plan-model';
import { ProjectModel } from 'src/app/common/models/project-model';

@Component({
  selector: 'app-plan-list',
  templateUrl: './plan-list.component.html',
  styleUrls: ['./plan-list.component.css']
})
export class PlanListComponent {

  @Input() project?: ProjectModel;
  @Input() planList?: PlanModel[];
  @Output() listRefreshEvent = new EventEmitter<void>();
  @Output() editPlanEvent = new EventEmitter<void>();
  @Output() deletePlanEvent = new EventEmitter<void>();
  @Output() createPlanEvent = new EventEmitter<void>();

  @Input() selectedPlan?: PlanModel;
  @Output() selectedPlanChange = new EventEmitter<PlanModel | undefined>();

  @Input() tableLoading?: boolean;
  @Input() deleteLoading?: boolean;


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

  onSelectedPlanFromList(plan: PlanModel) {
    this.selectedPlan = plan;
    this.selectedPlanChange.emit(this.selectedPlan);
  }

}
