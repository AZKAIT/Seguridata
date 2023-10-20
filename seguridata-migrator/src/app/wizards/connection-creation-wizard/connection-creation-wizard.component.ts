import { Component, EventEmitter, Input, OnDestroy, Output, ViewChild } from '@angular/core';
import { WizardStepContainerComponent } from '../wizard-step-container/wizard-step-container.component';
import { Subscription } from 'rxjs';
import { WizardStepConfig } from '../types/wizard-step-config';
import { WizardFormWrapper } from '../wrappers/wizard-form-wrapper';
import { ConnectionFormWrapperComponent } from '../wrappers/connection-form-wrapper/connection-form-wrapper.component';
import { Dialog } from 'primeng/dialog';
import { TableFormWrapperComponent } from '../wrappers/table-form-wrapper/table-form-wrapper.component';
import { ColumnFormWrapperComponent } from '../wrappers/column-form-wrapper/column-form-wrapper.component';

const connectionsName = 'Conexión';
const tablesName = 'Tablas';
const columnsName = 'Columnas';

@Component({
  selector: 'app-connection-creation-wizard',
  templateUrl: './connection-creation-wizard.component.html',
  styleUrls: ['./connection-creation-wizard.component.css']
})
export class ConnectionCreationWizardComponent implements OnDestroy {
  @ViewChild(WizardStepContainerComponent) wzStepContainer!: WizardStepContainerComponent;
  @ViewChild(Dialog, { static: true }) dialog!: Dialog;

  @Output() showWizardChange = new EventEmitter<boolean>();

  _showWizard: boolean = false;
  private subsList: Subscription[] = [];

  wzStepConfigs: WizardStepConfig[];
  stepInstances = new Map<string, WizardFormWrapper<any>>();

  constructor() {
    this.wzStepConfigs = [
      {
        stepName: connectionsName,
        stepComponent: ConnectionFormWrapperComponent
      },
      /*{
        stepName: tablesName,
        stepComponent: TableFormWrapperComponent
      },
      {
        stepName: columnsName,
        stepComponent: ColumnFormWrapperComponent
      }*/
    ];
  }

  ngOnDestroy(): void {
    let subs: Subscription | undefined;
    while (this.subsList.length) {
      subs = this.subsList.pop();
      if (subs) {
        subs.unsubscribe();
      }
    }
  }

  @Input()
  get showWizard() {
    return this._showWizard;
  }

  set showWizard(showWizard: boolean) {
    this._showWizard = showWizard;
  }

  onHideWizard() {
    this.showWizardChange.emit(this._showWizard);
  }

  syncComponents(componentCfg: WizardStepConfig) {
    if (componentCfg.stepInstance) {
      this.stepInstances.set(componentCfg.stepName, componentCfg.stepInstance);

      if (connectionsName === componentCfg.stepName) {
        this.prepareConnectionForm(componentCfg.stepInstance);
      } /*else if (tablesName === componentCfg.stepName) {
        this.preparePlanForm(componentCfg.stepInstance);
      } else if (columnsName === componentCfg.stepName) {
        this.prepareDefForm(componentCfg.stepInstance);
      }*/
    }
  }

  private prepareConnectionForm(component: WizardFormWrapper<any>) {
    const projectForm = component as ConnectionFormWrapperComponent;
    this.subsList.push(projectForm.getResult()
      .subscribe(projectResult => {
        console.log('Should close dialog')
        this.dialog.close(new MouseEvent('click'));
      }));
  }
}
