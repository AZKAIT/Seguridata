import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TablesContainerComponent } from './components/containers/tables-container/tables-container.component';
import { TableDataFormComponent } from './components/forms/table-data-form/table-data-form.component';
import { TableListComponent } from './components/lists/table-list/table-list.component';

import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { DialogModule } from 'primeng/dialog';
import { TooltipModule } from 'primeng/tooltip';

import { InputTextModule } from 'primeng/inputtext';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ColumnsModule } from '../columns/columns.module';


@NgModule({
  declarations: [
    TablesContainerComponent,
    TableDataFormComponent,
    TableListComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,

    ColumnsModule,

    TableModule,
    ButtonModule,
    CardModule,
    DialogModule,
    TooltipModule,

    InputTextModule,
    InputTextareaModule
  ],
  exports: [
    TablesContainerComponent
  ]
})
export class TablesModule { }
