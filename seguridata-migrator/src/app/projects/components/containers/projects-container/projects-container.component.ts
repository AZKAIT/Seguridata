import { Component, OnDestroy, OnInit } from '@angular/core';
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

  subsList: Subscription[] = [];

  projectList: ProjectModel[] = [];
  connList: ConnectionModel[] = [];

  selectedProject: ProjectModel | undefined;


  constructor(private _projectService: ProjectService, private _connService: ConnectionService) { }

  ngOnInit(): void {
    this.fetchProjects();

    this.subsList.push(this._connService.getAllConnections()
      .subscribe(connList => {
        this.connList = connList ?? [];
      }));
  }

  ngOnDestroy(): void {
    let subs: Subscription | undefined;
    while (this.subsList.length) {
      subs = this.subsList.pop();
      if (subs) {
        subs.unsubscribe();
      }
    }
  }

  onExecuteRefresh() {
    this.fetchProjects();
  }

  onEditProject() {
    console.log(`Execute onEditProject: ${JSON.stringify(this.selectedProject)}`);
  }

  onDeleteProject() {
    if (this.selectedProject?.id) {
      this.subsList.push(this._projectService.deleteProject(this.selectedProject.id)
        .subscribe(delProj => {
          if (this.selectedProject) {
            this.projectList.splice(this.projectList.indexOf(this.selectedProject, 1))
            this.selectedProject = undefined;
          }
        }));
    }
  }

  onCreateProject() {
    this.selectedProject = undefined;
  }

  saveProjectData(project: ProjectModel) {
    if (project?.id) {
      this.subsList.push(this._projectService.updateProject(project.id, project)
        .subscribe(updatedProj => {
          if (this.selectedProject && updatedProj) {
            const prevDataIndex = this.projectList.indexOf(this.selectedProject);
            this.projectList[prevDataIndex] = updatedProj;
            this.selectedProject = undefined;
          }
        }));
    } else {
      this.subsList.push(this._projectService.createProject(project)
        .subscribe(newProj => {
          if (newProj) {
            this.projectList.push(newProj);
            this.selectedProject = undefined;
          }
        }));
    }
  }

  onExecuteProjectEvent(projectId: string) {
    this.subsList.push(
      this._projectService.startProject(projectId).subscribe(response => {
        if (response) {
          this.changeStatusForLocalProject(projectId, ProjectStatus.STARTING); // TODO: Might ignore and manage with WebSocket-based impl
        }
      })
    );
  }

  onStopProjectEvent(projectId: string) {
    this.subsList.push(
      this._projectService.stopProject(projectId).subscribe(response => {
        if (response) {
          this.changeStatusForLocalProject(projectId, ProjectStatus.STOPPING); // TODO: Might ignore and manage with WebSocket-based impl
        }
      })
    );
  }


  private fetchProjects() {
    this.subsList.push(this._projectService.getAllProjects()
      .subscribe(projList => {
        this.projectList = projList ?? [];
      }));
  }

  private changeStatusForLocalProject(projectId: string, status: ProjectStatus) {
    const foundProject = this.projectList.find(project => project.id === projectId);
    if (foundProject) {
      foundProject.status = status;
    }
  }
}