import { Component, Input, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { ColumnModel } from 'src/app/common/models/column-model';
import { DefinitionModel } from 'src/app/common/models/definition-model';

import { PlanModel } from 'src/app/common/models/plan-model';
import { DefinitionService } from 'src/app/common/service/definition.service';
import { PlanService } from 'src/app/common/service/plan.service';
import { TableService } from 'src/app/common/service/table.service';

@Component({
  selector: 'app-definitions-container',
  templateUrl: './definitions-container.component.html',
  styleUrls: ['./definitions-container.component.css']
})
export class DefinitionsContainerComponent implements OnDestroy {

  subsList: Subscription[] = [];

  _plan: PlanModel | undefined;

  defList: DefinitionModel[] = [];
  sourceColumnList: ColumnModel[] = [];
  targetColumnList: ColumnModel[] = [];

  selectedDef: DefinitionModel | undefined;


  constructor(private _planService: PlanService, private _defService: DefinitionService, private _tableService: TableService) {
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
  get plan(): PlanModel | undefined { return this._plan; }
  set plan(plan: PlanModel | undefined) {
    this._plan = plan;
    this.fetchDefinitions(this._plan);
    this.fetchTableColumns(this._plan);
  }

  onExecuteRefresh() {
    this.fetchDefinitions(this._plan);
    this.fetchTableColumns(this._plan);
  }

  onEditDefinition() {
    console.log(`Execute onEditDefinition: ${JSON.stringify(this.selectedDef)}`);
  }

  onDeleteDef() {
    if (this.selectedDef?.id) {
      this.subsList.push(this._planService.deletePlan(this.selectedDef.id)
        .subscribe(delDef => {
          if (this.selectedDef) {
            this.defList.splice(this.defList.indexOf(this.selectedDef, 1))
            this.selectedDef = undefined;
          }
        }));
    }
  }

  onCreateDef() {
    this.selectedDef = undefined;
  }

  saveDefData(def: DefinitionModel) {
    if (def?.id) {
      this.subsList.push(this._defService.updateDefinition(def.id, def)
        .subscribe(updatedDef => {
          if (this.selectedDef && updatedDef) {
            const prevDataIndex = this.defList.indexOf(this.selectedDef);
            this.defList[prevDataIndex] = updatedDef;
            this.selectedDef = undefined;
          }
        }));
    } else if (this._plan?.id) {
      this.subsList.push(this._planService.createDefinitionForPlan(this._plan.id, def)
        .subscribe(newDef => {
          if (newDef) {
            this.defList.push(newDef);
            this.selectedDef = undefined;
          }
        }));
    } else {
      console.error('No Plan or Plan ID');
    }
  }


  private fetchDefinitions(plan: PlanModel | undefined) {
    if (plan?.id) {
      this.subsList.push(
        this._planService.getDefinitionsForPlan(plan.id)
          .subscribe(defList => {
            this.defList = defList ?? [];
          })
      );
    } else {
      this.defList = [];
      this.selectedDef = undefined;
    }
  }

  private fetchTableColumns(plan: PlanModel | undefined) {
    if (!plan) {
      this.sourceColumnList = [];
      this.targetColumnList = [];
      return;
    }

    if (plan.sourceTable?.id) {
      this.subsList.push(
        this._tableService.getColumnsForTable(plan.sourceTable?.id)
          .subscribe(columnList => {
            this.sourceColumnList = columnList ?? [];
          })
      );
    } else {
      console.error('No Source Table or Source Table ID');
    }

    if (plan.targetTable?.id) {
      this.subsList.push(
        this._tableService.getColumnsForTable(plan.targetTable?.id)
          .subscribe(columnList => {
            this.targetColumnList = columnList ?? [];
          })
      );
    } else {
      console.error('No Target Table or Target Table ID');
    }
  }
}
