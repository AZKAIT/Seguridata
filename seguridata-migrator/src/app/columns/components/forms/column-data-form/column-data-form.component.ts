import { Component, Input, Output, EventEmitter } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';

import { ColumnModel } from 'src/app/common/models/column-model';

@Component({
  selector: 'app-column-data-form',
  templateUrl: './column-data-form.component.html',
  styleUrls: ['./column-data-form.component.css']
})
export class ColumnDataFormComponent {

  @Output() saveColumn = new EventEmitter<ColumnModel>();

  _column: ColumnModel | undefined;

  columnFormGroup: FormGroup;

  constructor(private _formBuilder: FormBuilder) {
    this.columnFormGroup = this._formBuilder.group({
      id: [''],
      name: [''],
      description: [''],
      dataType: [''],
      dataLength: ['']
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
