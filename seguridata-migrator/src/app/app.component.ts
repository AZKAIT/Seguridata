import { Component, OnDestroy, OnInit } from '@angular/core';
import { NotificationsService } from './common/service/notifications.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'seguridata-migrator';

  constructor(private _notifService: NotificationsService) {
  }

  ngOnInit(): void {
    this._notifService.connectToNotifs();
  }

  ngOnDestroy(): void {
    this._notifService.closeConnection();
  }
}
