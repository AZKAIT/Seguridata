package com.seguridata.tools.dbmigrator.business.service;

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

    @Autowired
    public ErrorTrackingService(ErrorTrackingRepository errorTrackingRepo) {
        this.errorTrackingRepo = errorTrackingRepo;
    }

    public void createErrorTrackingForProject(ProjectEntity project, ErrorTrackingEntity errorTracking) {
        errorTracking.setDate(new Date());
        errorTracking.setId(null);
        errorTracking.setProject(project);

        this.errorTrackingRepo.createErrorTracking(errorTracking);
    }

    public List<ErrorTrackingEntity> getErrorTrackingForProject(String projectId) {
        return this.errorTrackingRepo.findErrorTrackingForProject(projectId);
    }
}
