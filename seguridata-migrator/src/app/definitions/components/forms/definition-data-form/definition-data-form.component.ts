import { Component, Input, Output, EventEmitter } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { DropdownChangeEvent } from 'primeng/dropdown';
import { ConversionFunction } from 'src/app/common/enums/conversion-function';
import { ColumnModel } from 'src/app/common/models/column-model';
import { DefinitionModel } from 'src/app/common/models/definition-model';

type SelectionTypes = {
  name: string;
  code: string;
};

@Component({
  selector: 'app-definition-data-form',
  templateUrl: './definition-data-form.component.html',
  styleUrls: ['./definition-data-form.component.css']
})
export class DefinitionDataFormComponent {

  @Input() sourceColumnList: ColumnModel[] = [];
  @Input() targetColumnList: ColumnModel[] = [];

  @Output() saveDef = new EventEmitter<DefinitionModel>();

  selSourceColumn?: ColumnModel;
  selTargetColumn?: ColumnModel;

  _definition: DefinitionModel | undefined;

  convFunctions: SelectionTypes[];

  defFormGroup: FormGroup;

  constructor(private _formBuilder: FormBuilder) {
    this.convFunctions = Object.keys(ConversionFunction).map((key, index) => {
      return {
        name: Object.values(ConversionFunction)[index],
        code: key
      }
    });

    this.defFormGroup = this._formBuilder.group({
      id: [''],
      conversionFunction: [''],
      sourceColumn: [undefined],
      targetColumn: [undefined]
    });
  }


  @Input()
  get definition(): DefinitionModel | undefined { return this._definition; }
  set definition(definition: DefinitionModel | undefined) {
    this._definition = definition;
    if (definition) {
      this.defFormGroup.patchValue(definition);
      this.selSourceColumn = definition.sourceColumn;
      this.selTargetColumn = definition.targetColumn;
    } else {
      this.defFormGroup.reset();
      this.defFormGroup.patchValue({sourceColumn: undefined, targetColumn: undefined});
    }
  }

  changeSourceColumn(ddEvent: DropdownChangeEvent): void {
    this.selSourceColumn = ddEvent.value;
  }

  changeTargetColumn(ddEvent: DropdownChangeEvent): void {
    this.selTargetColumn = ddEvent.value;
  }

  submit() {
    this.saveDef.emit(this.defFormGroup.value);
  }
}
