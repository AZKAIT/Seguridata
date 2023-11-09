import { Component, OnDestroy, OnInit } from '@angular/core';
import { NotificationsService } from './common/service/notifications.service';
import { PropertiesService } from './common/service/properties.service';
import { Subject, takeUntil } from 'rxjs';
import { SeguriDataConfigPropsModel } from './common/models/seguri-data-config-props-model';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'seguridata-migrator';

  private _unsubscribe$ = new Subject<void>();

  propertiesLoading: boolean = false;
  properties?: SeguriDataConfigPropsModel;

  constructor(private _notifService: NotificationsService, private _propsService: PropertiesService) {
  }

  ngOnInit(): void {
    this._notifService.connectToNotifs();

    this._propsService.propertiesLoadingObs()
    .pipe(takeUntil(this._unsubscribe$))
    .subscribe(propLoading => this.propertiesLoading = propLoading);

    this._propsService.propertiesObs()
    .pipe(takeUntil(this._unsubscribe$))
    .subscribe(props => {
      this.properties = props;
    });

    this._propsService.fetchProperties();
  }

  ngOnDestroy(): void {
    this._notifService.closeConnection();
  }
}
