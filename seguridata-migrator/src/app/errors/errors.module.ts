import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ErrorsContainerComponent } from './components/containers/errors-container/errors-container.component';
import { ErrorListComponent } from './components/lists/error-list/error-list.component';

import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';


@NgModule({
  declarations: [
    ErrorsContainerComponent,
    ErrorListComponent
  ],
  imports: [
    CommonModule,

    TableModule,
    ButtonModule,
    CardModule
  ],
  exports: [
    ErrorsContainerComponent
  ]
})
export class ErrorsModule { }
