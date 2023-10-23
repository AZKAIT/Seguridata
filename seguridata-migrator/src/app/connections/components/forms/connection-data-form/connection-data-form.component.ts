import { Component, Input, Output, EventEmitter } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DatabaseType } from 'src/app/common/enums/database-type';
import { ConnectionModel } from 'src/app/common/models/connection-model';
import { SelectionTypes } from 'src/app/common/types/selection-types';

@Component({
  selector: 'app-connection-data-form',
  templateUrl: './connection-data-form.component.html',
  styleUrls: ['./connection-data-form.component.css']
})
export class ConnectionDataFormComponent {

  @Output() saveConnection = new EventEmitter<ConnectionModel>();
  @Input() formLoading?: boolean;

  _connection: ConnectionModel | undefined;

  connFormGroup: FormGroup;
  dbTypes: SelectionTypes[];


  constructor(private _formBuilder: FormBuilder) {
    this.dbTypes = Object.keys(DatabaseType).map((key, index) => {
      return {
        name: Object.values(DatabaseType)[index],
        code: key
      }
    });

    this.connFormGroup = this._formBuilder.group({
      id: [''],
      name: ['', Validators.required],
      description: [''],
      host: ['', Validators.required],
      port: ['', Validators.required],
      objectService: [''],
      database: [''],
      username: ['', Validators.required],
      password: ['', Validators.required],
      type: ['', Validators.required],
      locked: ['']
    });
  }

  @Input()
  get connection(): ConnectionModel | undefined { return this._connection; }
  set connection(connection: ConnectionModel | undefined) {
    this._connection = connection;
    if (connection) {
      this.connFormGroup.patchValue(connection);
    } else {
      this.connFormGroup.reset();
    }
  }

  submit() {
    this.saveConnection.emit(this.connFormGroup.value);
  }
}
