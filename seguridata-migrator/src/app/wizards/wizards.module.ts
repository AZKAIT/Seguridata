import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ProjectCreationWizardComponent } from './project-creation-wizard/project-creation-wizard.component';
import { ProjectsModule } from '../projects/projects.module';
import { PlansModule } from '../plans/plans.module';
import { DefinitionsModule } from '../definitions/definitions.module';

import { DialogModule } from 'primeng/dialog';
import { StepsModule } from 'primeng/steps';
import { ButtonModule } from 'primeng/button';
import { TooltipModule } from 'primeng/tooltip';
import { WizardStepTargetDirective } from './wizard-step-target.directive';
import { WizardStepContainerComponent } from './wizard-step-container/wizard-step-container.component';
import { ProjectFormWrapperComponent } from './wrappers/project-form-wrapper/project-form-wrapper.component';
import { PlanFormWrapperComponent } from './wrappers/plan-form-wrapper/plan-form-wrapper.component';
import { DefinitionFormWrapperComponent } from './wrappers/definition-form-wrapper/definition-form-wrapper.component';
import { ConnectionFormWrapperComponent } from './wrappers/connection-form-wrapper/connection-form-wrapper.component';
import { TableFormWrapperComponent } from './wrappers/table-form-wrapper/table-form-wrapper.component';
import { ColumnFormWrapperComponent } from './wrappers/column-form-wrapper/column-form-wrapper.component';
import { ConnectionCreationWizardComponent } from './connection-creation-wizard/connection-creation-wizard.component';
import { ConnectionsModule } from '../connections/connections.module';
import { TablesModule } from '../tables/tables.module';
import { ColumnsModule } from '../columns/columns.module';


@NgModule({
  declarations: [
    ProjectCreationWizardComponent,
    WizardStepTargetDirective,
    WizardStepContainerComponent,
    ProjectFormWrapperComponent,
    PlanFormWrapperComponent,
    DefinitionFormWrapperComponent,
    ConnectionFormWrapperComponent,
    TableFormWrapperComponent,
    ColumnFormWrapperComponent,
    ConnectionCreationWizardComponent
  ],
  imports: [
    CommonModule,

    ProjectsModule,
    PlansModule,
    DefinitionsModule,
    ConnectionsModule,
    TablesModule,
    ColumnsModule,

    DialogModule,
    StepsModule,
    ButtonModule,
    TooltipModule
  ],
  exports: [
    ProjectCreationWizardComponent,
    ConnectionCreationWizardComponent
  ]
})
export class WizardsModule { }
