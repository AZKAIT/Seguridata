import { Component, EventEmitter, Input, Output, QueryList, ViewChildren } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { ColumnModel } from 'src/app/common/models/column-model';
import { DefinitionModel } from 'src/app/common/models/definition-model';
import { DefinitionDataRowComponent } from 'src/app/definitions/directives/definition-data-row/definition-data-row.component';

@Component({
  selector: 'app-definition-batch-form',
  templateUrl: './definition-batch-form.component.html',
  styleUrls: ['./definition-batch-form.component.css']
})
export class DefinitionBatchFormComponent {

  @ViewChildren(DefinitionDataRowComponent) defRows!: QueryList<DefinitionDataRowComponent>;

  @Input() sourceColumnList: ColumnModel[] = [];
  @Input() targetColumnList: ColumnModel[] = [];

  @Input() formLoading?: boolean;
  @Output() saveDefList = new EventEmitter<DefinitionModel[]>();

  defList: DefinitionModel[] = [];

  constructor(private _messageService: MessageService) {
  }

  createDef(): void {
    this.defList.push({});
  }

  submitDefinitions(): void {
    const defFormGroups: FormGroup[] = this.defRows.map(defRow => defRow.defFormGroup);

    if (defFormGroups.every(fg => fg.valid)) {
      this.saveDefList.next(defFormGroups.map(fg => fg.value));
    } else {
      this._messageService.add({ severity: 'error', summary: 'Definición inválida', detail: 'Existen campos pendientes en las Definiciones' })
    }
  }

  onRemoveDefinition(rowIndex: number) {
    this.defList.splice(rowIndex, 1);
  }
}
