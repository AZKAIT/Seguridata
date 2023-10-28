package com.seguridata.tools.dbmigrator.business.event;

import com.seguridata.tools.dbmigrator.data.entity.JobEntity;
import org.springframework.context.ApplicationEvent;

public class StopProjectEvent extends ApplicationEvent {
    private final JobEntity job;

    public StopProjectEvent(Object source, JobEntity job) {
        super(source);
        this.job = job;
    }

    public JobEntity getJob() {
        return job;
    }
}
