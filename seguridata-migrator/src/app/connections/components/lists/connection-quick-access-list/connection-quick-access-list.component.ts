import { Component, Input } from '@angular/core';
import { ConnectionModel } from 'src/app/common/models/connection-model';

@Component({
  selector: '[connQuickAccessList]',
  templateUrl: './connection-quick-access-list.component.html',
  styleUrls: ['./connection-quick-access-list.component.css']
})
export class ConnectionQuickAccessListComponent {
  @Input() connectionList?: ConnectionModel[];
  @Input() connectionsLoading: boolean = false;

}
