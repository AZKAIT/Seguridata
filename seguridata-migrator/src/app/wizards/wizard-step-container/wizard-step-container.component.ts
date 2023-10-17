import { Component, ComponentRef, Input, Output, OnDestroy, OnInit, ViewChild, EventEmitter } from '@angular/core';
import { MenuItem } from 'primeng/api';
import { WizardStepTargetDirective } from '../wizard-step-target.directive';
import { WizardStepConfig } from '../types/wizard-step-config';
import { WizardFormWrapper } from '../wrappers/wizard-form-wrapper';
import { Subscription } from 'rxjs';
import { Steps } from 'primeng/steps';

@Component({
  selector: 'app-wizard-step-container',
  templateUrl: './wizard-step-container.component.html',
  styleUrls: ['./wizard-step-container.component.css']
})
export class WizardStepContainerComponent implements OnInit, OnDestroy {
  @ViewChild(WizardStepTargetDirective, {static: true}) wzStepTarget!: WizardStepTargetDirective;
  @ViewChild(Steps, {static: true}) stepsComponent!: Steps;

  @Input() wizardConfigs!: WizardStepConfig[];
  @Output() onLoadedComponent = new EventEmitter<WizardStepConfig>();

  activeIndex: number = 0;

  private subsList: Subscription[] = [];

  wizardSteps: MenuItem[] = [];
  existingComponents: ComponentRef<WizardFormWrapper<any>>[] = [];

  ngOnInit(): void {
      if (this.wizardConfigs) {
        const viewContainerRef = this.wzStepTarget.viewContainerRef;

        for (let index in this.wizardConfigs) {
          const i = Number(index);
          const config = this.wizardConfigs[i];
          this.existingComponents[i] = viewContainerRef.createComponent(config.stepComponent);
          this.wizardSteps[i] = this.mapConfigsToMenus(i, config);

          const compInstance = this.existingComponents[i].instance;
          compInstance.setIndex(i);
          compInstance.setName(config.stepName);
          this.subsList.push(compInstance.getIndexEmitter().subscribe(index => {
            let newIndex;
            if (index < 0) {
              newIndex= 0;
            } else if (index >= this.existingComponents.length) {
              newIndex = this.existingComponents.length - 1;
            } else {
              newIndex = index;
            }
            this.stepsComponent.onItemClick(new MouseEvent('click'), this.wizardSteps[newIndex], newIndex);
          }));

          config.stepInstance = compInstance;
          this.onLoadedComponent.next(config);
        }

        const containerLength = viewContainerRef.length;
        for (let i = 0; i < (containerLength-1); i++) {
          viewContainerRef.detach();
        }
      } else {
        alert('Not working');
      }
  }

  ngOnDestroy(): void {
    let subs: Subscription | undefined;
    while (this.subsList.length) {
      subs = this.subsList.pop();
      if (subs) {
        subs.unsubscribe();
      }
    }

    const viewContainerRef = this.wzStepTarget.viewContainerRef;
    viewContainerRef.clear();
    this.onLoadedComponent.complete();
  }


  private mapConfigsToMenus(menuIndex: number, config: WizardStepConfig): MenuItem {
    return {
      label: config.stepName,
      command: () => this.replaceComponent(menuIndex)
    }
  }

  private replaceComponent(menuIndex: number) {
    const viewContainerRef = this.wzStepTarget.viewContainerRef;
    viewContainerRef.detach();

    const comp = this.existingComponents[menuIndex];
    if (comp) {
      viewContainerRef.insert(comp.hostView);
    }
  }
}
