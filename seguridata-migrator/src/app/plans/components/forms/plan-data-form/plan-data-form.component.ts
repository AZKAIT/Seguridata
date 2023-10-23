import { Component, Input, Output, EventEmitter } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DropdownChangeEvent } from 'primeng/dropdown';

import { PlanModel } from 'src/app/common/models/plan-model';
import { TableModel } from 'src/app/common/models/table-model';

@Component({
  selector: 'app-plan-data-form',
  templateUrl: './plan-data-form.component.html',
  styleUrls: ['./plan-data-form.component.css']
})
export class PlanDataFormComponent {

  @Input() sourceTableList: TableModel[] = [];
  @Input() targetTableList: TableModel[] = [];

  @Output() savePlan = new EventEmitter<PlanModel>();

  @Input() formLoading?: boolean;
  @Input() showForm?: boolean;

  selSourceTable?: TableModel;
  selTargetTable?: TableModel;

  _plan: PlanModel | undefined;

  planFormGroup: FormGroup;

  constructor(private _formBuilder: FormBuilder) {
    this.planFormGroup = this._formBuilder.group({
      id: [''],
      orderNum: ['', Validators.required],
      sourceTable: [undefined, Validators.required],
      targetTable: [undefined, Validators.required],
      initialSkip: ['', Validators.required],
      rowLimit: ['', Validators.required],
      maxRows: ['', Validators.required]
    });
  }


  @Input()
  get plan(): PlanModel | undefined { return this._plan; }
  set plan(plan: PlanModel | undefined) {
    this._plan = plan;
    if (plan) {
      this.planFormGroup.patchValue(plan);
      this.selSourceTable = plan.sourceTable;
      this.selTargetTable = plan.targetTable;
    } else {
      this.planFormGroup.reset();
      this.planFormGroup.patchValue({sourceTable: undefined, targetTable: undefined});
    }
  }

  changeSourceTable(ddEvent: DropdownChangeEvent): void {
    this.selSourceTable = ddEvent.value;
  }

  changeTargetTable(ddEvent: DropdownChangeEvent): void {
    this.selTargetTable = ddEvent.value;
  }

  submit() {
    this.savePlan.emit(this.planFormGroup.value);
  }
}
