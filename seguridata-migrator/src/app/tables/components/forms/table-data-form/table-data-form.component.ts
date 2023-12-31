import { Component, Input, Output, EventEmitter } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ColumnModel } from 'src/app/common/models/column-model';
import { TableModel } from 'src/app/common/models/table-model';

@Component({
  selector: 'app-table-data-form',
  templateUrl: './table-data-form.component.html',
  styleUrls: ['./table-data-form.component.css']
})
export class TableDataFormComponent {

  @Output() saveTable = new EventEmitter<TableModel>();

  @Input() formLoading?: boolean;
  @Input() showForm?: boolean;

  // TODO: Remove when getting rid of order_column
  @Input() columnList?: ColumnModel[];

  _table: TableModel | undefined;

  tableFormGroup: FormGroup;

  constructor(private _formBuilder: FormBuilder) {
    this.tableFormGroup = this._formBuilder.group({
      id: [''],
      schema: [''],
      name: ['', Validators.required],
      description: [''],
      orderColumnName: [undefined]
    });
  }


  @Input()
  get table(): TableModel | undefined { return this._table; }
  set table(table: TableModel | undefined) {
    this._table = table;
    if (table) {
      this.tableFormGroup.patchValue(table);
    } else {
      this.tableFormGroup.reset();
    }
  }

  submit() {
    this.saveTable.emit(this.tableFormGroup.value);
  }

}
