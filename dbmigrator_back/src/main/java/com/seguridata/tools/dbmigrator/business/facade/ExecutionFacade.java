package com.seguridata.tools.dbmigrator.business.facade;

import com.seguridata.tools.dbmigrator.business.exception.EmptyResultException;
import com.seguridata.tools.dbmigrator.business.factory.ThreadPoolExecutorFactory;
import com.seguridata.tools.dbmigrator.business.manager.DatabaseQueryManager;
import com.seguridata.tools.dbmigrator.business.service.ConnectionService;
import com.seguridata.tools.dbmigrator.business.service.JobService;
import com.seguridata.tools.dbmigrator.business.service.ProjectService;
import com.seguridata.tools.dbmigrator.business.service.ErrorTrackingService;
import com.seguridata.tools.dbmigrator.data.constant.ExecutionResult;
import com.seguridata.tools.dbmigrator.business.task.PlanExecutionCallable;
import com.seguridata.tools.dbmigrator.business.thread.MigrationThreadPoolExecutor;
import com.seguridata.tools.dbmigrator.data.constant.JobStatus;
import com.seguridata.tools.dbmigrator.data.entity.ConnectionEntity;
import com.seguridata.tools.dbmigrator.data.entity.ErrorTrackingEntity;
import com.seguridata.tools.dbmigrator.data.entity.JobEntity;
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
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Component
public class ExecutionFacade {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionFacade.class);

    private final ApplicationContext appContext;
    private final ProjectService projectService;
    private final ConnectionService connectionService;
    private final JobService jobService;
    private final ErrorTrackingService errorTrackingService;
    private final ThreadPoolExecutorFactory threadPoolExecutorFactory;

    @Autowired
    public ExecutionFacade(ApplicationContext appContext,
                           ProjectService projectService,
                           ConnectionService connectionService,
                           JobService jobService,
                           ErrorTrackingService errorTrackingService,
                           ThreadPoolExecutorFactory threadPoolExecutorFactory) {
        this.appContext = appContext;
        this.projectService = projectService;
        this.connectionService = connectionService;
        this.jobService = jobService;
        this.errorTrackingService = errorTrackingService;
        this.threadPoolExecutorFactory = threadPoolExecutorFactory;
    }

    public void startExecution(JobEntity job) {
        ProjectEntity project = job.getProject();
        LOGGER.info("Starting execution of Job: {}", job.getProjectExecutionNumber());
        ConnectionEntity srcConn = null;
        ConnectionEntity tgtConn = null;

        JobStatus jobResult = JobStatus.FINISHED_WARN;
        try {
            this.jobService.updateProjectStatus(job, JobStatus.STARTING);
            srcConn = project.getSourceConnection();
            tgtConn = project.getTargetConnection();
            this.connectionService.lockConnections(srcConn, tgtConn);

            LOGGER.debug("Initializing Source Query Manager");
            DatabaseQueryManager sourceQueryManager = this.getQueryManager(job, srcConn);
            LOGGER.debug("Initializing Target Query Manager");
            DatabaseQueryManager targetQueryManager = this.getQueryManager(job, tgtConn);

            List<PlanExecutionCallable> executionCallables = this.getTasksForProject(job, sourceQueryManager, targetQueryManager);
            MigrationThreadPoolExecutor executor = this.executeTasks(job, executionCallables);
            this.jobService.updateProjectStatus(job, JobStatus.RUNNING);

            LOGGER.info("Awaiting execution...");
            executor.getLatch().await();
            LOGGER.info("Finished waiting");


            sourceQueryManager.closeConnection();
            targetQueryManager.closeConnection();

            jobResult = this.resolveStatusByResult(executor.getFutureList());
            LOGGER.info("Execution finished");
        } catch (Exception e) {
            LOGGER.error("Exception on Job execution: Job({}) -> {}", job.getProjectExecutionNumber(), e.getMessage());
            jobResult = JobStatus.FINISHED_ERROR;
        } finally {
            this.connectionService.unlockConnections(srcConn, tgtConn);
            this.projectService.updateProjectLocked(project, false);

            this.jobService.updateProjectStatus(job, jobResult);
            this.threadPoolExecutorFactory.removeExecutorForProject(project.getId());
            LOGGER.info("Execution of Project: {} is now unlocked", project.getId());
        }
    }

    public void stopExecution(JobEntity job) {
        try {
            this.jobService.updateProjectStatus(job, JobStatus.STOPPING);
            MigrationThreadPoolExecutor threadPoolExecutor = this.threadPoolExecutorFactory
                    .getExecutorForProject(job.getId());

            threadPoolExecutor.stopTasks();
        } catch (Exception e) {
            LOGGER.error("Exception on Project STOP execution: Project({}) -> {}", job.getProjectExecutionNumber(), e.getMessage());
        }
    }

    private DatabaseQueryManager getQueryManager(JobEntity job, ConnectionEntity connection) {
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
            this.errorTrackingService.createErrorTrackingForProject(job, errorTracking);

            throw new RuntimeException("Falló la inicialización del manejador de dato de Base Destino");
        }
    }

    private List<PlanExecutionCallable> getTasksForProject(JobEntity job,
                                                           DatabaseQueryManager sourceQueryManager,
                                                           DatabaseQueryManager targetQueryManager) {
        LOGGER.debug("Creating Tasks for Project");
        try {
            if (Objects.isNull(job.getProject()) || CollectionUtils.isEmpty(job.getProject().getPlans())) {
                throw new EmptyResultException("La lista de Planes está vacía");
            }

            return job.getProject().getPlans().stream()
                    .sorted(Comparator.comparing(PlanEntity::getOrderNum))
                    .map(plan -> new PlanExecutionCallable(job, plan,
                            sourceQueryManager, targetQueryManager, this.errorTrackingService))
                    .collect(Collectors.toList());
        } catch (EmptyResultException e) {
            LOGGER.error("Exception on Plans: {}", e.getMessage());

            ErrorTrackingEntity errorTracking = new ErrorTrackingEntity();
            errorTracking.setMessage(e.getMessage());
            errorTracking.setJob(job);
            errorTracking.setReferenceType(ProjectEntity.class.getCanonicalName());
            errorTracking.setReferenceId(job.getProject().getId());
            this.errorTrackingService.createErrorTrackingForProject(job, errorTracking);

            throw new RuntimeException(e);
        } catch (Exception e) {
            LOGGER.error("Error creating tasks for Project: {}", e.getMessage());

            ErrorTrackingEntity errorTracking = new ErrorTrackingEntity();
            errorTracking.setMessage(e.getMessage());
            errorTracking.setJob(job);
            errorTracking.setReferenceType(e.getClass().getCanonicalName());
            this.errorTrackingService.createErrorTrackingForProject(job, errorTracking);

            throw new RuntimeException(e);
        }
    }

    private MigrationThreadPoolExecutor executeTasks(JobEntity job, List<PlanExecutionCallable> executionCallables) {
        LOGGER.debug("Executing Tasks");
        try {
            MigrationThreadPoolExecutor executorService = this.threadPoolExecutorFactory
                    .initializeExecutorForJob(job);
            executorService.invokePlanTasks(executionCallables);

            LOGGER.info("Tasks executed successfully");
            return executorService;
        } catch (Exception e) {
            LOGGER.error("Exception executing tasks: {}", e.getMessage());
            ErrorTrackingEntity errorTracking = new ErrorTrackingEntity();
            errorTracking.setMessage(e.getMessage());
            errorTracking.setJob(job);
            errorTracking.setReferenceType(e.getClass().getCanonicalName());
            this.errorTrackingService.createErrorTrackingForProject(job, errorTracking);

            throw new RuntimeException(e);
        }
    }

    private JobStatus resolveStatusByResult(List<Future<ExecutionResult>> results) {
        List<ExecutionResult> execResults = results.stream().map(future -> {
            try {
                return future.get();
            } catch (Exception e) {
                LOGGER.warn("Exception while retrieving results, marking as WARN result");
                return ExecutionResult.EXCEPTION;
            }
        }).collect(Collectors.toList());

        if (execResults.stream().anyMatch(ExecutionResult.INTERRUPTED::equals)) {
            return JobStatus.STOPPED;
        }

        if (execResults.stream().allMatch(ExecutionResult.SUCCESS::equals)) {
            return JobStatus.FINISHED_SUCCESS;
        }

        if (execResults.stream().allMatch(ExecutionResult.EXCEPTION::equals)) {
            return JobStatus.FINISHED_ERROR;
        }

        return JobStatus.FINISHED_WARN;
    }
}
