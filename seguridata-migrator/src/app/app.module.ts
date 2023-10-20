import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { APP_BASE_HREF, PlatformLocation } from '@angular/common';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { AppComponent } from './app.component';
import { ConnectionsModule } from './connections/connections.module';
import { ProjectsModule } from './projects/projects.module';
import { ColumnsModule } from './columns/columns.module';
import { PlansModule } from './plans/plans.module';
import { DefinitionsModule } from './definitions/definitions.module';
import { GlobalHttpInterceptorService } from './common/interceptor/global-http-interceptor.service';
import { WizardsModule } from './wizards/wizards.module';
import { AppRoutingModule } from './app-routing.module';
import { DashboardComponent } from './dashboard/dashboard.component';

import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { ButtonModule } from 'primeng/button';
import { TabViewModule } from 'primeng/tabview';
import { ImageModule } from 'primeng/image';

@NgModule({
  declarations: [
    AppComponent,
    DashboardComponent
  ],
  imports: [
    AppRoutingModule,
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    ConnectionsModule,

    ColumnsModule,
    ProjectsModule,
    PlansModule,
    DefinitionsModule,
    WizardsModule,

    ToastModule,
    ButtonModule,
    TabViewModule,
    ImageModule
  ],
  providers: [
    {
      provide: APP_BASE_HREF,
      useFactory: (s: PlatformLocation) => s.getBaseHrefFromDOM(),
        deps: [PlatformLocation]
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: GlobalHttpInterceptorService,
      multi: true
    },
    MessageService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
