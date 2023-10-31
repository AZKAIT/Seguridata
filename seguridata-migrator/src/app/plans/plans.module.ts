import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { PlansContainerComponent } from './components/containers/plans-container/plans-container.component';
import { PlanDataFormComponent } from './components/forms/plan-data-form/plan-data-form.component';
import { PlanListComponent } from './components/lists/plan-list/plan-list.component';
import { DefinitionsModule } from '../definitions/definitions.module';

import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { DialogModule } from 'primeng/dialog';

import { InputTextModule } from 'primeng/inputtext';
import { DropdownModule } from 'primeng/dropdown';
import { InputNumberModule } from 'primeng/inputnumber';
import { TooltipModule } from 'primeng/tooltip';




@NgModule({
  declarations: [
    PlansContainerComponent,
    PlanDataFormComponent,
    PlanListComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,

    DefinitionsModule,

    TableModule,
    ButtonModule,
    CardModule,
    DialogModule,

    InputTextModule,
    DropdownModule,
    InputNumberModule,
    TooltipModule
  ],
  exports: [
    PlansContainerComponent,
    PlanDataFormComponent
  ]
})
export class PlansModule { }
