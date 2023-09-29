package com.seguridata.tools.dbmigrator.business.event;

import com.seguridata.tools.dbmigrator.data.entity.ProjectEntity;
import org.springframework.context.ApplicationEvent;

public class InitiateProjectEvent extends ApplicationEvent {
    private final ProjectEntity project;

    public InitiateProjectEvent(Object source, ProjectEntity project) {
        super(source);
        this.project = project;
    }

    public ProjectEntity getProject() {
        return project;
    }
}
