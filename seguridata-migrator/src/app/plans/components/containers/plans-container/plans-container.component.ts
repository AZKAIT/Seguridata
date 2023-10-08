import { Component, Input, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { PlanModel } from 'src/app/common/models/plan-model';

import { ProjectModel } from 'src/app/common/models/project-model';
import { TableModel } from 'src/app/common/models/table-model';
import { ConnectionService } from 'src/app/common/service/connection.service';
import { PlanService } from 'src/app/common/service/plan.service';
import { ProjectService } from 'src/app/common/service/project.service';

@Component({
  selector: 'app-plans-container',
  templateUrl: './plans-container.component.html',
  styleUrls: ['./plans-container.component.css']
})
export class PlansContainerComponent implements OnDestroy {

  subsList: Subscription[] = [];

  _project: ProjectModel | undefined;

  planList: PlanModel[] = [];
  sourceTableList: TableModel[] = [];
  targetTableList: TableModel[] = [];

  selectedPlan: PlanModel | undefined;

  constructor(private _projectService: ProjectService, private _planService: PlanService, private _connService: ConnectionService) {
  }

  ngOnDestroy(): void {
    let subs: Subscription | undefined;
    while (this.subsList.length) {
      subs = this.subsList.pop();
      if (subs) {
        subs.unsubscribe();
      }
    }
  }

  @Input()
  get project(): ProjectModel | undefined { return this._project; }
  set project(project: ProjectModel | undefined) {
    this._project = project;
    this.fetchPlans(this._project);
    this.fetchConnTables(this._project);
  }

  onExecuteRefresh() {
    this.fetchPlans(this._project);
    this.fetchConnTables(this._project);
  }

  onEditPlan() {
    console.log(`Execute onEditPlan: ${JSON.stringify(this.selectedPlan)}`);
  }

  onDeletePlan() {
    if (this.selectedPlan?.id) {
      this.subsList.push(this._planService.deletePlan(this.selectedPlan.id)
        .subscribe(delPlan => {
          if (this.selectedPlan) {
            this.planList.splice(this.planList.indexOf(this.selectedPlan, 1))
            this.selectedPlan = undefined;
          }
        }));
    }
  }

  onCreatePlan() {
    this.selectedPlan = undefined;
  }

  savePlanData(plan: PlanModel) {
    if (plan?.id) {
      this.subsList.push(this._planService.updatePlan(plan.id, plan)
        .subscribe(updatedPlan => {
          if (this.selectedPlan && updatedPlan) {
            const prevDataIndex = this.planList.indexOf(this.selectedPlan);
            this.planList[prevDataIndex] = updatedPlan;
            this.selectedPlan = undefined;
          }
        }));
    } else if (this._project?.id) {
      this.subsList.push(this._projectService.createPlanForProject(this._project.id, plan)
        .subscribe(newPlan => {
          if (newPlan) {
            this.planList.push(newPlan);
            this.selectedPlan = undefined;
          }
        }));
    } else {
      console.error('No Project or Project ID');
    }
  }



  private fetchPlans(project: ProjectModel | undefined) {
    if (project?.id) {
      this.subsList.push(
        this._projectService.getPlansForProject(project.id)
          .subscribe(planList => {
            this.planList = planList ?? [];
          })
      );
    } else {
      this.planList = [];
      this.selectedPlan = undefined;
    }
  }

  private fetchConnTables(project: ProjectModel | undefined) {
    if (!project) {
      this.sourceTableList = [];
      this.targetTableList = [];
      return;
    }

    if (project.sourceConnection?.id) {
      this.subsList.push(
        this._connService.getTablesForConnection(project.sourceConnection?.id)
          .subscribe(tableList => {
            this.sourceTableList = tableList ?? [];
          })
      );
    } else {
      console.error('No Source connection or Source connection ID');
    }

    if (project.targetConnection?.id) {
      this.subsList.push(
        this._connService.getTablesForConnection(project.targetConnection?.id)
          .subscribe(tableList => {
            this.targetTableList = tableList ?? [];
          })
      );
    } else {
      console.error('No Target connection or Target connection ID');
    }
  }
}
