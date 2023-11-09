import { Injectable } from '@angular/core';
import { PropertiesClientService } from '../client/properties-client.service';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { SeguriDataConfigPropsModel } from '../models/seguri-data-config-props-model';
import { MessageService } from 'primeng/api';

@Injectable({
  providedIn: 'root'
})
export class PropertiesService {

  private propertiesLoading$ = new BehaviorSubject<boolean>(false);
  private properties$ = new BehaviorSubject<SeguriDataConfigPropsModel | undefined>(undefined);

  private subs!: Subscription;

  constructor(private _propertiesClient: PropertiesClientService, private _messageService: MessageService) { }


  fetchProperties(): void {
    if (this.subs && !this.subs.closed) {
      this.subs.unsubscribe();
    }

    this.propertiesLoading$.next(true);
    this.subs = this._propertiesClient.getProperties()
      .subscribe({
        next: properties => {
          if (properties) {
            this.properties$.next(properties);
          }
          this.propertiesLoading$.next(false);
        },
        error: err => {
          this.postError('Error al cargar las Propiedades', err?.messages?.join(','));
          this.propertiesLoading$.next(false);
        }
      });
  }

  propertiesLoadingObs(): Observable<boolean> {
    return this.propertiesLoading$.asObservable();
  }

  propertiesObs(): Observable<SeguriDataConfigPropsModel | undefined> {
    return this.properties$.asObservable();
  }

  private postError(title: string, message: string) {
    this._messageService.add({ severity: 'error', summary: title, detail: message });
  }
}
