import { Observable } from "rxjs";

export interface WizardFormWrapper<T> {
  setIndex(index: number): void;
  getIndexEmitter(): Observable<number>;
  emitNextIndex(): void;
  emitPreviousIndex(): void;
  setName(name: string): void;
  getName(): string;
  getResult(): Observable<T>;
}
