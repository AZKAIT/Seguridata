import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { PlansContainerComponent } from './components/containers/plans-container/plans-container.component';
import { PlanDataFormComponent } from './components/forms/plan-data-form/plan-data-form.component';
import { PlanListComponent } from './components/lists/plan-list/plan-list.component';

import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';

import { InputTextModule } from 'primeng/inputtext';
import { DropdownModule } from 'primeng/dropdown';
import { InputNumberModule } from 'primeng/inputnumber';
import { DefinitionsModule } from '../definitions/definitions.module';



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

    InputTextModule,
    DropdownModule,
    InputNumberModule
  ],
  exports: [
    PlansContainerComponent
  ]
})
export class PlansModule { }
