import { Component, Input, Output, EventEmitter, ViewChild, OnDestroy } from '@angular/core';
import { WizardStepConfig } from '../types/wizard-step-config';
import { WizardStepContainerComponent } from '../wizard-step-container/wizard-step-container.component';
import { ProjectFormWrapperComponent } from '../wrappers/project-form-wrapper/project-form-wrapper.component';
import { PlanFormWrapperComponent } from '../wrappers/plan-form-wrapper/plan-form-wrapper.component';
import { DefinitionFormWrapperComponent } from '../wrappers/definition-form-wrapper/definition-form-wrapper.component';
import { WizardFormWrapper } from '../wrappers/wizard-form-wrapper';
import { Subscription } from 'rxjs';
import { Dialog } from 'primeng/dialog';

const projectsName = 'Proyectos';
const plansName = 'Planes';
const defsName = 'Definiciones';

@Component({
  selector: 'app-project-creation-wizard',
  templateUrl: './project-creation-wizard.component.html',
  styleUrls: ['./project-creation-wizard.component.css']
})
export class ProjectCreationWizardComponent implements OnDestroy {
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
        stepName: projectsName,
        stepComponent: ProjectFormWrapperComponent
      },
      {
        stepName: plansName,
        stepComponent: PlanFormWrapperComponent
      },
      {
        stepName: defsName,
        stepComponent: DefinitionFormWrapperComponent
      }
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

  syncComponents(componentCfg: WizardStepConfig) {
    if (componentCfg.stepInstance) {
      this.stepInstances.set(componentCfg.stepName, componentCfg.stepInstance);

      if (projectsName === componentCfg.stepName) {
        this.prepareProjectForm(componentCfg.stepInstance);
      } else if (plansName === componentCfg.stepName) {
        this.preparePlanForm(componentCfg.stepInstance);
      } else if (defsName === componentCfg.stepName) {
        this.prepareDefForm(componentCfg.stepInstance);
      }
    }
  }

  onHideWizard() {
    this.showWizardChange.emit(this._showWizard);
  }

  private prepareProjectForm(component: WizardFormWrapper<any>) {
    const projectForm = component as ProjectFormWrapperComponent;
    this.subsList.push(projectForm.getResult()
      .subscribe(projectResult => {
        const planForm = this.stepInstances.get(plansName) as PlanFormWrapperComponent;

        if (projectResult.autoPopulate) {
          this.dialog.close(new MouseEvent('click'));
        }

        planForm.inputProject = projectResult;
      }));
  }

  private preparePlanForm(component: WizardFormWrapper<any>) {
    const planForm = component as PlanFormWrapperComponent;
    this.subsList.push(planForm.getResult()
      .subscribe(planResult => {
        const defForm = this.stepInstances.get(defsName) as DefinitionFormWrapperComponent;
        defForm.inputPlan = planResult;
      }));
  }

  private prepareDefForm(component: WizardFormWrapper<any>) {
    const defForm = component as DefinitionFormWrapperComponent;
    this.subsList.push(defForm.getResult()
    .subscribe(defsResult => {
      this.dialog.close(new MouseEvent('click'));
    }));
  }
}
