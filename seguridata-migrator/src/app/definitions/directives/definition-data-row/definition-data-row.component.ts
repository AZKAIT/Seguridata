import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ConversionFunction } from 'src/app/common/enums/conversion-function';
import { ColumnModel } from 'src/app/common/models/column-model';
import { SelectionTypes } from 'src/app/common/types/selection-types';

@Component({
  selector: '[defDataRow]',
  templateUrl: './definition-data-row.component.html',
  styleUrls: ['./definition-data-row.component.css']
})
export class DefinitionDataRowComponent {
  @Input() sourceColumnList: ColumnModel[] = [];
  @Input() targetColumnList: ColumnModel[] = [];

  @Output() removeDefinition = new EventEmitter<void>();

  convFunctions: SelectionTypes[];
  defFormGroup: FormGroup;

  selSourceColumn?: ColumnModel;
  selTargetColumn?: ColumnModel;
  selFunction?: ConversionFunction;

  constructor(private _formBuilder: FormBuilder) {
    this.convFunctions = Object.keys(ConversionFunction).map((key, index) => {
      return {
        name: Object.values(ConversionFunction)[index],
        code: key
      }
    });

    this.defFormGroup = this._formBuilder.group({
      id: [''],
      conversionFunction: [ConversionFunction.NONE],
      sourceColumn: [undefined],
      targetColumn: [undefined]
    });
  }

  changeSourceColumn(): void {
    this.defFormGroup.patchValue({sourceColumn: this.selSourceColumn});
  }

  changeTargetColumn(): void {
    this.defFormGroup.patchValue({targetColumn: this.selTargetColumn});
  }

  changeConversionFunc(): void {
    this.defFormGroup.patchValue({conversionFunction: this.selFunction});
  }

  onRemoveDefinitionRow(): void {
    this.removeDefinition.next();
  }
}
