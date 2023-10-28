package com.seguridata.tools.dbmigrator.business.service;

import com.seguridata.tools.dbmigrator.business.client.StompMessageClient;
import com.seguridata.tools.dbmigrator.data.entity.ErrorTrackingEntity;
import com.seguridata.tools.dbmigrator.data.entity.JobEntity;
import com.seguridata.tools.dbmigrator.data.repository.ErrorTrackingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ErrorTrackingService {

    private final ErrorTrackingRepository errorTrackingRepo;
    private final StompMessageClient stompMsgClient;

    @Autowired
    public ErrorTrackingService(ErrorTrackingRepository errorTrackingRepo,
                                StompMessageClient stompMsgClient) {
        this.errorTrackingRepo = errorTrackingRepo;
        this.stompMsgClient = stompMsgClient;
    }

    public void createErrorTrackingForProject(JobEntity job, ErrorTrackingEntity errorTracking) {
        errorTracking.setDate(new Date());
        errorTracking.setId(null);
        errorTracking.setJob(job);

        ErrorTrackingEntity errorTrack = this.errorTrackingRepo.createErrorTracking(errorTracking);
        this.stompMsgClient.sendJobExecutionError(job, errorTrack);
    }

    public List<ErrorTrackingEntity> getErrorTrackingForJob(JobEntity job) {
        return this.errorTrackingRepo.findErrorTrackingForJob(job.getId());
    }
}
