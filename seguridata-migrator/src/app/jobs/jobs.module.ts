import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { JobsContainerComponent } from './components/containers/jobs-container/jobs-container.component';
import { JobListComponent } from './components/lists/job-list/job-list.component';

import { ErrorsModule } from '../errors/errors.module';


import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { ProgressBarModule } from 'primeng/progressbar';
import { TooltipModule } from 'primeng/tooltip';



@NgModule({
  declarations: [
    JobsContainerComponent,
    JobListComponent
  ],
  imports: [
    CommonModule,

    ErrorsModule,

    TableModule,
    ButtonModule,
    CardModule,
    ProgressBarModule,
    TooltipModule
  ]
})
export class JobsModule { }
