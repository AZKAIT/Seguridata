import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ConnectionListComponent } from './components/lists/connection-list/connection-list.component';
import { ConnectionsContainerComponent } from './components/containers/connections-container/connections-container.component';

import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { DropdownModule } from 'primeng/dropdown';
import { InputTextModule } from 'primeng/inputtext';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { InputNumberModule } from 'primeng/inputnumber';
import { PasswordModule } from 'primeng/password';
import { ConnectionDataFormComponent } from './components/forms/connection-data-form/connection-data-form.component';

import { TablesModule } from '../tables/tables.module';



@NgModule({
  declarations: [
    ConnectionListComponent,
    ConnectionsContainerComponent,
    ConnectionDataFormComponent
  ],
  imports: [
    FormsModule,
    ReactiveFormsModule,
    CommonModule,

    TablesModule,

    TableModule,
    ButtonModule,
    CardModule,

    DropdownModule,
    InputTextModule,
    InputTextareaModule,
    InputNumberModule,
    PasswordModule
  ],
  exports: [
    ConnectionsContainerComponent
  ]
})
export class ConnectionsModule { }
