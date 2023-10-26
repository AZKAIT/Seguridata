package com.seguridata.tools.dbmigrator.business.facade;

import com.seguridata.tools.dbmigrator.business.exception.EmptyResultException;
import com.seguridata.tools.dbmigrator.business.factory.ThreadPoolExecutorFactory;
import com.seguridata.tools.dbmigrator.business.manager.DatabaseQueryManager;
import com.seguridata.tools.dbmigrator.business.service.ConnectionService;
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
    private final ConnectionService connectionService;
    private final ErrorTrackingService errorTrackingService;
    private final ThreadPoolExecutorFactory threadPoolExecutorFactory;

    @Autowired
    public ExecutionFacade(ApplicationContext appContext,
                           ProjectService projectService,
                           ConnectionService connectionService,
                           ErrorTrackingService errorTrackingService,
                           ThreadPoolExecutorFactory threadPoolExecutorFactory) {
        this.appContext = appContext;
        this.projectService = projectService;
        this.connectionService = connectionService;
        this.errorTrackingService = errorTrackingService;
        this.threadPoolExecutorFactory = threadPoolExecutorFactory;
    }

    public void startExecution(ProjectEntity project) {
        LOGGER.info("Starting execution of Project: {}", project.getId());
        ConnectionEntity srcConn = null;
        ConnectionEntity tgtConn = null;
        try {
            this.projectService.updateProjectStatus(project, ProjectStatus.STARTING);
            srcConn = project.getSourceConnection();
            tgtConn = project.getTargetConnection();
            this.connectionService.lockConnections(srcConn, tgtConn);

            LOGGER.debug("Initializing Source Query Manager");
            DatabaseQueryManager sourceQueryManager = this.getQueryManager(project, srcConn);
            LOGGER.debug("Initializing Target Query Manager");
            DatabaseQueryManager targetQueryManager = this.getQueryManager(project, tgtConn);

            List<PlanExecutionCallable> executionCallables = this.getTasksForProject(project, sourceQueryManager, targetQueryManager);
            CountDownLatch latch = this.executeTasks(project, executionCallables);
            this.projectService.updateProjectStatus(project, ProjectStatus.RUNNING);

            LOGGER.info("Awaiting execution...");
            latch.await();
            LOGGER.info("Finished waiting");


            sourceQueryManager.closeConnection();
            targetQueryManager.closeConnection();
            LOGGER.info("Execution finished");
        } catch (Exception e) {
            LOGGER.error("Exception on Project execution: Project({}) -> {}", project.getId(), e.getMessage());
        } finally {
            this.connectionService.unlockConnections(srcConn, tgtConn);
            this.projectService.updateProjectStatus(project, ProjectStatus.STOPPED);
            this.threadPoolExecutorFactory.removeExecutorForProject(project.getId());
            LOGGER.info("Execution of Project: {} is now unlocked", project.getId());
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

    private DatabaseQueryManager getQueryManager(ProjectEntity project, ConnectionEntity connection) {
        try {
            DatabaseQueryManager targetQueryManager = this.appContext.getBean(DatabaseQueryManager.class, this.appContext);
            LOGGER.debug("Initializing Connection");
            targetQueryManager.initializeConnection(connection.getId());
            LOGGER.info("Connection Initialized successfully: {} - ({})", connection.getName(), connection.getId());

            return targetQueryManager;
        } catch (Exception e) {
            LOGGER.error("Error caught on getQueryManager: {}", e.getMessage());
            ErrorTrackingEntity errorTracking = new ErrorTrackingEntity();
            errorTracking.setMessage(e.getMessage());
            errorTracking.setReferenceType(ConnectionEntity.class.getCanonicalName());
            errorTracking.setReferenceId(connection.getId());
            this.errorTrackingService.createErrorTrackingForProject(project, errorTracking);

            throw new RuntimeException("Falló la inicialización del manejador de dato de Base Destino");
        }
    }

    private List<PlanExecutionCallable> getTasksForProject(ProjectEntity project,
                                                           DatabaseQueryManager sourceQueryManager,
                                                           DatabaseQueryManager targetQueryManager) {
        LOGGER.debug("Creating Tasks for Project");
        try {
            List<PlanEntity> plans = project.getPlans();
            if (CollectionUtils.isEmpty(plans)) {
                throw new EmptyResultException("La lista de Planes está vacía");
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
