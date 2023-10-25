import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ColumnsContainerComponent } from './components/containers/columns-container/columns-container.component';
import { ColumnDataFormComponent } from './components/forms/column-data-form/column-data-form.component';
import { ColumnListComponent } from './components/lists/column-list/column-list.component';


import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { DialogModule } from 'primeng/dialog';
import { DropdownModule } from 'primeng/dropdown';
import { InputTextModule } from 'primeng/inputtext';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { TooltipModule } from 'primeng/tooltip';



@NgModule({
  declarations: [
    ColumnsContainerComponent,
    ColumnDataFormComponent,
    ColumnListComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,

    TableModule,
    ButtonModule,
    CardModule,
    DialogModule,
    DropdownModule,
    InputTextModule,
    InputTextareaModule,
    TooltipModule
  ],
  exports: [
    ColumnsContainerComponent
  ]
})
export class ColumnsModule { }
