import { Directive, ViewContainerRef } from '@angular/core';

@Directive({
  selector: '[wzStepTarget]'
})
export class WizardStepTargetDirective {

  constructor(public viewContainerRef: ViewContainerRef) { }

}
