import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { DefinitionsContainerComponent } from './components/containers/definitions-container/definitions-container.component';
import { DefinitionDataFormComponent } from './components/forms/definition-data-form/definition-data-form.component';
import { DefinitionListComponent } from './components/lists/definition-list/definition-list.component';

import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { DialogModule } from 'primeng/dialog';
import { DragDropModule } from 'primeng/dragdrop';
import { InputTextModule } from 'primeng/inputtext';
import { DropdownModule } from 'primeng/dropdown';
import { ScrollerModule } from 'primeng/scroller';
import { TooltipModule } from 'primeng/tooltip';
import { DefinitionBatchFormComponent } from './components/forms/definition-batch-form/definition-batch-form.component';
import { DefinitionDataRowComponent } from './directives/definition-data-row/definition-data-row.component';



@NgModule({
  declarations: [
    DefinitionsContainerComponent,
    DefinitionDataFormComponent,
    DefinitionListComponent,
    DefinitionBatchFormComponent,
    DefinitionDataRowComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,

    TableModule,
    ButtonModule,
    CardModule,
    DialogModule,
    ScrollerModule,
    TooltipModule,

    InputTextModule,
    DropdownModule,
    DragDropModule
  ],
  exports: [
    DefinitionsContainerComponent,
    DefinitionBatchFormComponent
  ]
})
export class DefinitionsModule { }
