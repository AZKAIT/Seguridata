import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { DefinitionsContainerComponent } from './components/containers/definitions-container/definitions-container.component';
import { DefinitionDataFormComponent } from './components/forms/definition-data-form/definition-data-form.component';
import { DefinitionListComponent } from './components/lists/definition-list/definition-list.component';

import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';

import { InputTextModule } from 'primeng/inputtext';
import { DropdownModule } from 'primeng/dropdown';



@NgModule({
  declarations: [
    DefinitionsContainerComponent,
    DefinitionDataFormComponent,
    DefinitionListComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,

    TableModule,
    ButtonModule,
    CardModule,

    InputTextModule,
    DropdownModule
  ],
  exports: [
    DefinitionsContainerComponent
  ]
})
export class DefinitionsModule { }
