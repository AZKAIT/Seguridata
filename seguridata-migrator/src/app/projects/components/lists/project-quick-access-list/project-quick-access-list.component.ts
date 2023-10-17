import { Component, Input } from '@angular/core';
import { ProjectModel } from 'src/app/common/models/project-model';

@Component({
  selector: '[projQuickAccessList]',
  templateUrl: './project-quick-access-list.component.html',
  styleUrls: ['./project-quick-access-list.component.css']
})
export class ProjectQuickAccessListComponent {
  @Input() projectList?: ProjectModel[];
  @Input() projectsLoading: boolean = false;
}
