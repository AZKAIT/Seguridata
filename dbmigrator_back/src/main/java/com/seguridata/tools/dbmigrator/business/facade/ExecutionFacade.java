package com.seguridata.tools.dbmigrator.business.facade;

import com.seguridata.tools.dbmigrator.business.manager.DatabaseQueryManager;
import com.seguridata.tools.dbmigrator.business.service.ProjectService;
import com.seguridata.tools.dbmigrator.business.service.ErrorTrackingService;
import com.seguridata.tools.dbmigrator.business.service.TableService;
import com.seguridata.tools.dbmigrator.business.task.PlanExecutionCallable;
import com.seguridata.tools.dbmigrator.data.entity.ConnectionEntity;
import com.seguridata.tools.dbmigrator.data.entity.ErrorTrackingEntity;
import com.seguridata.tools.dbmigrator.data.entity.PlanEntity;
import com.seguridata.tools.dbmigrator.data.entity.ProjectEntity;
import com.seguridata.tools.dbmigrator.data.entity.TableEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ExecutionFacade {

    private final ApplicationContext appContext;
    private final ErrorTrackingService errorTrackingService;

    @Autowired
    public ExecutionFacade(ApplicationContext appContext,
                           ErrorTrackingService errorTrackingService) {
        this.appContext = appContext;
        this.errorTrackingService = errorTrackingService;
    }

    public void startExecution(ProjectEntity project) {
        DatabaseQueryManager sourceQueryManager = this.getSourceQueryManager(project);
        DatabaseQueryManager targetQueryManager = this.getTargetQueryManager(project);

        List<PlanEntity> plans = project.getPlans();
        if (CollectionUtils.isEmpty(plans)) {
            ErrorTrackingEntity errorTracking = new ErrorTrackingEntity();
            errorTracking.setMessage("Plan List is empty");
            this.errorTrackingService.createErrorTrackingForProject(project, errorTracking);
            return;
        }

        List<PlanExecutionCallable> executionCallables = plans.stream()
                .map(plan -> new PlanExecutionCallable(plan, sourceQueryManager, targetQueryManager, this.errorTrackingService))
                .collect(Collectors.toList());

        // TODO: send callables to the ThreadPool
    }

    private DatabaseQueryManager getSourceQueryManager(ProjectEntity project) {
        ConnectionEntity sourceConnection = project.getSourceConnection();
        try {
            DatabaseQueryManager sourceQueryManager = this.appContext.getBean(DatabaseQueryManager.class, this.appContext);
            sourceQueryManager.initializeConnection(sourceConnection.getId());

            return sourceQueryManager;
        } catch (Exception e) {
            ErrorTrackingEntity errorTracking = new ErrorTrackingEntity();
            errorTracking.setMessage(e.getMessage());
            errorTracking.setReferenceType(ConnectionEntity.class.getCanonicalName());
            errorTracking.setReferenceId(sourceConnection.getId());
            this.errorTrackingService.createErrorTrackingForProject(project, errorTracking);

            throw new RuntimeException("Initialization of Source Query Manager failed");
        }
    }

    private DatabaseQueryManager getTargetQueryManager(ProjectEntity project) {
        ConnectionEntity targetConnection = project.getTargetConnection();
        try {
            DatabaseQueryManager targetQueryManager = this.appContext.getBean(DatabaseQueryManager.class, this.appContext);
            targetQueryManager.initializeConnection(targetConnection.getId());

            return targetQueryManager;
        } catch (Exception e) {
            ErrorTrackingEntity errorTracking = new ErrorTrackingEntity();
            errorTracking.setMessage(e.getMessage());
            errorTracking.setReferenceType(ConnectionEntity.class.getCanonicalName());
            errorTracking.setReferenceId(targetConnection.getId());
            this.errorTrackingService.createErrorTrackingForProject(project, errorTracking);

            throw new RuntimeException("Initialization of Target Query Manager failed");
        }
    }
}
