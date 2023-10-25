import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ProjectsContainerComponent } from './components/containers/projects-container/projects-container.component';
import { ProjectDataFormComponent } from './components/forms/project-data-form/project-data-form.component';
import { ProjectListComponent } from './components/lists/project-list/project-list.component';
import { ConnectionsModule } from '../connections/connections.module';
import { PlansModule } from '../plans/plans.module';
import { ProjectQuickAccessListComponent } from './components/lists/project-quick-access-list/project-quick-access-list.component';

import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { StepsModule } from 'primeng/steps';
import { DropdownModule } from 'primeng/dropdown';
import { InputTextModule } from 'primeng/inputtext';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { DialogModule } from 'primeng/dialog';
import { ToggleButtonModule } from 'primeng/togglebutton';
import { TooltipModule } from 'primeng/tooltip';


@NgModule({
  declarations: [
    ProjectsContainerComponent,
    ProjectDataFormComponent,
    ProjectListComponent,
    ProjectQuickAccessListComponent
  ],
  imports: [
    FormsModule,
    ReactiveFormsModule,
    CommonModule,
    ConnectionsModule,
    PlansModule,

    TableModule,
    ButtonModule,
    CardModule,
    DialogModule,

    DropdownModule,
    InputTextModule,
    InputTextareaModule,
    ToggleButtonModule,
    TooltipModule,

    StepsModule
  ],
  exports: [
    ProjectsContainerComponent,
    ProjectDataFormComponent,
    ProjectQuickAccessListComponent
  ]
})
export class ProjectsModule { }
