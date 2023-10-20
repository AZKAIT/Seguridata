package com.seguridata.tools.dbmigrator.business.service;

import com.seguridata.tools.dbmigrator.business.client.StompMessageClient;
import com.seguridata.tools.dbmigrator.data.entity.ErrorTrackingEntity;
import com.seguridata.tools.dbmigrator.data.entity.ProjectEntity;
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

    public void createErrorTrackingForProject(ProjectEntity project, ErrorTrackingEntity errorTracking) {
        errorTracking.setDate(new Date());
        errorTracking.setId(null);
        errorTracking.setProject(project);

        ErrorTrackingEntity errorTrack = this.errorTrackingRepo.createErrorTracking(errorTracking);
        this.stompMsgClient.sendProjectExecutionError(project, errorTrack);
    }

    public List<ErrorTrackingEntity> getErrorTrackingForProject(String projectId) {
        return this.errorTrackingRepo.findErrorTrackingForProject(projectId);
    }
}
