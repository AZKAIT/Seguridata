package com.seguridata.tools.dbmigrator.business.event;

import com.seguridata.tools.dbmigrator.data.entity.JobEntity;
import org.springframework.context.ApplicationEvent;

public class InitiateProjectEvent extends ApplicationEvent {
    private final JobEntity job;

    public InitiateProjectEvent(Object source, JobEntity job) {
        super(source);
        this.job = job;
    }

    public JobEntity getJob() {
        return job;
    }
}
