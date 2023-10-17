import { Component, OnDestroy, OnInit } from '@angular/core';
import { ConnectionService } from '../common/service/connection.service';
import { MessageService } from 'primeng/api';
import { Subscription } from 'rxjs';
import { ConnectionModel } from '../common/models/connection-model';
import { ProjectModel } from '../common/models/project-model';
import { ProjectService } from '../common/service/project.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit, OnDestroy {

  private _subsList: Subscription[] = [];

  connsLoading: boolean = false;
  connectionList: ConnectionModel[] = [];

  projsLoading: boolean = false;
  projectList: ProjectModel[] = [];

  showProjectWizard = false;
  showConnWizard = false;

  constructor(private _connectionService: ConnectionService, private _projectService: ProjectService, private _messageService: MessageService) {
  }

  ngOnInit(): void {
    this.fetchConnections();
    this.fetchProjects();
  }

  ngOnDestroy(): void {
    let subs: Subscription | undefined;
    while (this._subsList.length) {
      subs = this._subsList.pop();
      if (subs) {
        subs.unsubscribe();
      }
    }
  }

  private fetchConnections() {
    this.connsLoading = true;
    this._subsList.push(this._connectionService.getAllConnections()
      .subscribe({
        next: connList => {
          this.connectionList = connList ?? [];
          this.connsLoading = false;
        },
        error: err => {
          this.postError('Error al cargar Conexiones', err?.messages?.join(','));
          this.connsLoading = false;
        }
      }));
  }

  private fetchProjects() {
    this.projsLoading = true;
    this._subsList.push(this._projectService.getAllProjects()
      .subscribe({
        next: projList => {
          this.projectList = projList ?? [];
          this.projsLoading = false;
        },
        error: err => {
          this.projsLoading = false;
          this.postError('Error al cargar Proyectos', err?.messages?.join(','));
        }
      }));
  }

  private postError(title: string, message: string) {
    this._messageService.add({ severity: 'error', summary: title, detail: message });
  }

  private postSuccess(title: string, message: string) {
    this._messageService.add({ severity: 'success', summary: title, detail: message });
  }



  onShowProjectWizard() {
    this.showProjectWizard = true;
  }

  onShowConnectionWizard() {
    this.showConnWizard = true;
  }
}
