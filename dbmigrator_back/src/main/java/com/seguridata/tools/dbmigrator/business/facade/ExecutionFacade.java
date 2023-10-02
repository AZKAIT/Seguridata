package com.seguridata.tools.dbmigrator.business.facade;

import com.seguridata.tools.dbmigrator.business.exception.EmptyResultException;
import com.seguridata.tools.dbmigrator.business.factory.ThreadPoolExecutorFactory;
import com.seguridata.tools.dbmigrator.business.manager.DatabaseQueryManager;
import com.seguridata.tools.dbmigrator.business.service.ProjectService;
import com.seguridata.tools.dbmigrator.business.service.ErrorTrackingService;
import com.seguridata.tools.dbmigrator.business.task.PlanExecutionCallable;
import com.seguridata.tools.dbmigrator.business.thread.MigrationThreadPoolExecutor;
import com.seguridata.tools.dbmigrator.data.constant.ProjectStatus;
import com.seguridata.tools.dbmigrator.data.entity.ConnectionEntity;
import com.seguridata.tools.dbmigrator.data.entity.ErrorTrackingEntity;
import com.seguridata.tools.dbmigrator.data.entity.PlanEntity;
import com.seguridata.tools.dbmigrator.data.entity.ProjectEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

@Component
public class ExecutionFacade {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionFacade.class);

    private final ApplicationContext appContext;
    private final ProjectService projectService;
    private final ErrorTrackingService errorTrackingService;
    private final ThreadPoolExecutorFactory threadPoolExecutorFactory;

    @Autowired
    public ExecutionFacade(ApplicationContext appContext,
                           ProjectService projectService,
                           ErrorTrackingService errorTrackingService,
                           ThreadPoolExecutorFactory threadPoolExecutorFactory) {
        this.appContext = appContext;
        this.projectService = projectService;
        this.errorTrackingService = errorTrackingService;
        this.threadPoolExecutorFactory = threadPoolExecutorFactory;
    }

    public void startExecution(ProjectEntity project) {
        LOGGER.info("Starting execution of Project: {}", project.getId());
        try {
            this.projectService.updateProjectStatus(project, ProjectStatus.STARTING);
            DatabaseQueryManager sourceQueryManager = this.getSourceQueryManager(project);
            DatabaseQueryManager targetQueryManager = this.getTargetQueryManager(project);

            List<PlanExecutionCallable> executionCallables = this.getTasksForProject(project, sourceQueryManager, targetQueryManager);
            CountDownLatch latch = this.executeTasks(project, executionCallables);
            this.projectService.updateProjectStatus(project, ProjectStatus.RUNNING);

            LOGGER.info("Awaiting execution...");
            latch.await();
            LOGGER.info("Finished waiting, removing Executor");
            this.threadPoolExecutorFactory.removeExecutorForProject(project.getId());

            this.projectService.updateProjectStatus(project, ProjectStatus.STOPPED);
            LOGGER.info("Execution finished");
        } catch (Exception e) {
            LOGGER.error("Exception on Project execution: Project({}) -> {}", project.getId(), e.getMessage());
            this.projectService.updateProjectStatus(project, ProjectStatus.STOPPED);
        }
    }

    public void stopExecution(ProjectEntity project) {
        try {
            this.projectService.updateProjectStatus(project, ProjectStatus.STOPPING);
            MigrationThreadPoolExecutor threadPoolExecutor = this.threadPoolExecutorFactory
                    .getExecutorForProject(project.getId(), false);

            threadPoolExecutor.stopTasks();
        } catch (Exception e) {
            LOGGER.error("Exception on Project STOP execution: Project({}) -> {}", project.getId(), e.getMessage());
        }
    }

    private DatabaseQueryManager getSourceQueryManager(ProjectEntity project) {
        LOGGER.debug("Inside getSourceQueryManager");
        ConnectionEntity sourceConnection = project.getSourceConnection();
        try {
            DatabaseQueryManager sourceQueryManager = this.appContext.getBean(DatabaseQueryManager.class, this.appContext);
            LOGGER.debug("Initializing Connection");
            sourceQueryManager.initializeConnection(sourceConnection.getId());
            LOGGER.info("Connection Initialized successfully: {} - ({})", sourceConnection.getName(), sourceConnection.getId());

            return sourceQueryManager;
        } catch (Exception e) {
            LOGGER.error("Error caught on getSourceQueryManager: {}", e.getMessage());
            ErrorTrackingEntity errorTracking = new ErrorTrackingEntity();
            errorTracking.setMessage(e.getMessage());
            errorTracking.setReferenceType(ConnectionEntity.class.getCanonicalName());
            errorTracking.setReferenceId(sourceConnection.getId());
            this.errorTrackingService.createErrorTrackingForProject(project, errorTracking);

            throw new RuntimeException("Initialization of Source Query Manager failed");
        }
    }

    private DatabaseQueryManager getTargetQueryManager(ProjectEntity project) {
        LOGGER.debug("Inside getTargetQueryManager");
        ConnectionEntity targetConnection = project.getTargetConnection();
        try {
            DatabaseQueryManager targetQueryManager = this.appContext.getBean(DatabaseQueryManager.class, this.appContext);
            LOGGER.debug("Initializing Connection");
            targetQueryManager.initializeConnection(targetConnection.getId());
            LOGGER.info("Connection Initialized successfully: {} - ({})", targetConnection.getName(), targetConnection.getId());

            return targetQueryManager;
        } catch (Exception e) {
            LOGGER.error("Error caught on getTargetQueryManager: {}", e.getMessage());
            ErrorTrackingEntity errorTracking = new ErrorTrackingEntity();
            errorTracking.setMessage(e.getMessage());
            errorTracking.setReferenceType(ConnectionEntity.class.getCanonicalName());
            errorTracking.setReferenceId(targetConnection.getId());
            this.errorTrackingService.createErrorTrackingForProject(project, errorTracking);

            throw new RuntimeException("Initialization of Target Query Manager failed");
        }
    }

    private List<PlanExecutionCallable> getTasksForProject(ProjectEntity project,
                                                           DatabaseQueryManager sourceQueryManager,
                                                           DatabaseQueryManager targetQueryManager) {
        LOGGER.debug("Creating Tasks for Project");
        try {
            List<PlanEntity> plans = project.getPlans();
            if (CollectionUtils.isEmpty(plans)) {
                throw new EmptyResultException("Plan list is empty");
            }

            return plans.stream()
                    .sorted(Comparator.comparing(PlanEntity::getOrderNum))
                    .map(plan -> new PlanExecutionCallable(project, plan,
                            sourceQueryManager, targetQueryManager, this.errorTrackingService))
                    .collect(Collectors.toList());
        } catch (EmptyResultException e) {
            LOGGER.error("Exception on Plans: {}", e.getMessage());

            ErrorTrackingEntity errorTracking = new ErrorTrackingEntity();
            errorTracking.setMessage(e.getMessage());
            errorTracking.setProject(project);
            errorTracking.setReferenceType(ProjectEntity.class.getCanonicalName());
            errorTracking.setReferenceId(project.getId());
            this.errorTrackingService.createErrorTrackingForProject(project, errorTracking);

            throw new RuntimeException(e);
        } catch (Exception e) {
            LOGGER.error("Error creating tasks for Project: {}", e.getMessage());

            ErrorTrackingEntity errorTracking = new ErrorTrackingEntity();
            errorTracking.setMessage(e.getMessage());
            errorTracking.setProject(project);
            errorTracking.setReferenceType(e.getClass().getCanonicalName());
            this.errorTrackingService.createErrorTrackingForProject(project, errorTracking);

            throw new RuntimeException(e);
        }
    }

    private CountDownLatch executeTasks(ProjectEntity project, List<PlanExecutionCallable> executionCallables) {
        LOGGER.debug("Executing Tasks");
        try {
            MigrationThreadPoolExecutor executorService = this.threadPoolExecutorFactory.getExecutorForProject(project.getId(), true);
            CountDownLatch latch = executorService.invokePlanTasks(executionCallables);

            LOGGER.info("Tasks executed successfully");
            return latch;
        } catch (Exception e) {
            LOGGER.error("Exception executing tasks: {}", e.getMessage());
            ErrorTrackingEntity errorTracking = new ErrorTrackingEntity();
            errorTracking.setMessage(e.getMessage());
            errorTracking.setProject(project);
            errorTracking.setReferenceType(e.getClass().getCanonicalName());
            this.errorTrackingService.createErrorTrackingForProject(project, errorTracking);

            throw new RuntimeException(e);
        }
    }
}
