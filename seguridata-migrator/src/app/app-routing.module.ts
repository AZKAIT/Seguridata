import { NgModule } from '@angular/core';
import { Routes, RouterModule, PreloadAllModules } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';
import { ConnectionsContainerComponent } from './connections/components/containers/connections-container/connections-container.component';
import { ProjectsContainerComponent } from './projects/components/containers/projects-container/projects-container.component';


const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'connections', component: ConnectionsContainerComponent },
  { path: 'projects', component: ProjectsContainerComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { preloadingStrategy: PreloadAllModules, onSameUrlNavigation: 'reload' })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
