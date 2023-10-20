import { Component, OnDestroy, OnInit } from '@angular/core';
import { MessageService } from 'primeng/api';
import { Subscription } from 'rxjs';
import { ProjectStatus } from 'src/app/common/enums/project-status';
import { ConnectionModel } from 'src/app/common/models/connection-model';
import { ProjectModel } from 'src/app/common/models/project-model';
import { ConnectionService } from 'src/app/common/service/connection.service';
import { ProjectService } from 'src/app/common/service/project.service';

@Component({
  selector: 'app-projects-container',
  templateUrl: './projects-container.component.html',
  styleUrls: ['./projects-container.component.css']
})
export class ProjectsContainerComponent implements OnInit, OnDestroy {

  private _subsList: Subscription[] = [];

  projectList: ProjectModel[] = [];
  connList: ConnectionModel[] = [];

  selectedProject: ProjectModel | undefined;

  showForm: boolean = false;

  tableLoading: boolean = false;
  formLoading: boolean = false;
  deleteLoading: boolean = false;
  schedulingLoading: boolean = false;


  constructor(private _projectService: ProjectService, private _connService: ConnectionService, private _messageService: MessageService) { }

  ngOnInit(): void {
    this.fetchProjects();
    this.fetchConnections();
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

  onExecuteRefresh() {
    this.fetchProjects();
  }

  onEditProject() {
    this.showForm = true;
  }

  onDeleteProject() {
    this.deleteLoading = true;
    if (this.selectedProject?.id) {
      this._subsList.push(this._projectService.deleteProject(this.selectedProject.id)
        .subscribe({
          next: delProj => {
            if (this.selectedProject) {
              this.projectList.splice(this.projectList.indexOf(this.selectedProject), 1);
              this.selectedProject = undefined;
              this.postSuccess('Eliminar Proyecto', `Proyecto ${delProj?.name} eliminado`);
            }
            this.deleteLoading = false;
          },
          error: err => {
            this.postError('Error al eliminar Proyecto', err?.messages?.join(','));
            this.deleteLoading = false;
          }
        }));
    }
  }

  onCreateProject() {
    this.selectedProject = undefined;
    this.showForm = true;
  }

  saveProjectData(project: ProjectModel) {
    this.formLoading = true;
    if (project?.id) {
      this._subsList.push(this._projectService.updateProject(project.id, project)
        .subscribe({
          next: updatedProj => {
            if (this.selectedProject && updatedProj) {
              const prevDataIndex = this.projectList.indexOf(this.selectedProject);
              this.projectList[prevDataIndex] = updatedProj;
              this.selectedProject = undefined;
              this.showForm = false;
              this.postSuccess('Actualizar Proyecto', `Proyecto ${updatedProj?.name} actualizado`);
            }
            this.formLoading = false;
          },
          error: err => {
            this.formLoading = false;
            this.postError('Error al actualizar Proyecto', err?.messages?.join(','));
          }
        }));
    } else {
      this._subsList.push(this._projectService.createProject(project)
        .subscribe({
          next: newProj => {
            if (newProj) {
              this.projectList.push(newProj);
              this.selectedProject = undefined;
              this.showForm = false;
              this.postSuccess('Crear Proyecto', `Proyecto ${newProj?.name} creado`);
            }
            this.formLoading = false;
          },
          error: err => {
            this.formLoading = false;
            this.postError('Error al crear Proyecto', err?.messages?.join(','));
          }
        }));
    }
  }

  onExecuteProjectEvent() {
    this.schedulingLoading = true;
    const projectId = this.selectedProject?.id;
    if (!projectId) {
      this.postError('Error al agendar ejecución de Proyecto', 'No se ha seleccionado un proyecto');
      this.schedulingLoading = false;
      return;
    }
    this._subsList.push(
      this._projectService.startProject(projectId)
        .subscribe({
          next: response => {
            if (response) {
              this.changeStatusForLocalProject(projectId, ProjectStatus.STARTING);
              this._messageService.add({ severity: 'info', summary: 'Proyecto Agendado', detail: 'El Proyecto se agendó para Iniciar' });
            }
            this.schedulingLoading = false;
            this.selectedProject = undefined;
          },
          error: err => {
            this.postError('Error al agendar Proyecto', err?.messages?.join(','));
            this.schedulingLoading = false;
            this.selectedProject = undefined;
          }
        })
    );
  }

  onStopProjectEvent() {
    this.schedulingLoading = true;
    const projectId = this.selectedProject?.id;
    if (!projectId) {
      this.postError('Error al agendar interrupción de Proyecto', 'No se ha seleccionado un proyecto');
      this.schedulingLoading = false;
      return;
    }
    this._subsList.push(
      this._projectService.stopProject(projectId)
        .subscribe({
          next: response => {
            if (response) {
              this.changeStatusForLocalProject(projectId, ProjectStatus.STOPPING);
              this._messageService.add({ severity: 'info', summary: 'Proyecto Agendado', detail: 'El Proyecto se agendó para Finalizar' });
            }
            this.schedulingLoading = false;
            this.selectedProject = undefined;
          },
          error: err => {
            this.postError('Error al agendar Proyecto', err?.messages?.join(','));
            this.schedulingLoading = false;
            this.selectedProject = undefined;
          }
        })
    );
  }


  private fetchProjects() {
    this.selectedProject = undefined;
    this.tableLoading = true;
    this._subsList.push(this._projectService.getAllProjects()
      .subscribe({
        next: projList => {
          this.projectList = projList ?? [];
          this.tableLoading = false;
        },
        error: err => {
          this.tableLoading = false;
          this.postError('Error al cargar Proyectos', err?.messages?.join(','));
        }
      }));
  }

  private fetchConnections() {
    this._subsList.push(this._connService.getAllConnections()
      .subscribe({
        next: connList => {
          this.connList = connList ?? [];
        },
        error: err => {
          this.postError('Error al cargar Conexiones', err?.messages?.join(','));
        }
      }));
  }

  private changeStatusForLocalProject(projectId: string, status: ProjectStatus) {
    const foundProject = this.projectList.find(project => project.id === projectId);
    if (foundProject) {
      foundProject.status = status;
    }
  }

  private postError(title: string, message: string) {
    this._messageService.add({ severity: 'error', summary: title, detail: message });
  }

  private postSuccess(title: string, message: string) {
    this._messageService.add({ severity: 'success', summary: title, detail: message });
  }
}
