import { Component, Input, Output, EventEmitter } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ColumnDataType } from 'src/app/common/enums/column-data-type';

import { ColumnModel } from 'src/app/common/models/column-model';
import { SelectionTypes } from 'src/app/common/types/selection-types';


@Component({
  selector: 'app-column-data-form',
  templateUrl: './column-data-form.component.html',
  styleUrls: ['./column-data-form.component.css']
})
export class ColumnDataFormComponent {

  @Output() saveColumn = new EventEmitter<ColumnModel>();

  @Input() formLoading?: boolean;
  @Input() showForm?: boolean;

  _column: ColumnModel | undefined;

  columnFormGroup: FormGroup;

  dataTypes: SelectionTypes[];

  constructor(private _formBuilder: FormBuilder) {
    this.dataTypes = Object.keys(ColumnDataType).map((key, index) => {
      return {
        name: Object.values(ColumnDataType)[index],
        code: key
      }
    });

    this.columnFormGroup = this._formBuilder.group({
      id: [''],
      name: ['', Validators.required],
      description: [''],
      dataType: ['', Validators.required],
      dataLength: ['', Validators.required]
    });
  }


  @Input()
  get column(): ColumnModel | undefined { return this._column; }
  set column(column: ColumnModel | undefined) {
    this._column = column;
    if (column) {
      this.columnFormGroup.patchValue(column);
    } else {
      this.columnFormGroup.reset();
    }
  }

  submit() {
    this.saveColumn.emit(this.columnFormGroup.value);
  }
}
