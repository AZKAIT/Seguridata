import { Type } from "@angular/core"
import { WizardFormWrapper } from "../wrappers/wizard-form-wrapper"

export type WizardStepConfig = {
  stepName: string,
  stepComponent: Type<WizardFormWrapper<any>>,
  stepInstance?: WizardFormWrapper<any>
}
