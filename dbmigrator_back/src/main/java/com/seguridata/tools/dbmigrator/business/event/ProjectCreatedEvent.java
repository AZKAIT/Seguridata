package com.seguridata.tools.dbmigrator.business.event;

import com.seguridata.tools.dbmigrator.data.model.ProjectModel;
import org.springframework.context.ApplicationEvent;

public class ProjectCreatedEvent extends ApplicationEvent {
    private final ProjectModel projectModel;

    public ProjectCreatedEvent(Object source,
                               ProjectModel projectModel) {
        super(source);
        this.projectModel = projectModel;
    }

    public ProjectModel getProjectModel() {
        return projectModel;
    }
}
