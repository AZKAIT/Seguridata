package com.seguridata.tools.dbmigrator.business.task;

import com.seguridata.tools.dbmigrator.business.exception.EmptyResultException;
import com.seguridata.tools.dbmigrator.business.manager.DatabaseQueryManager;
import com.seguridata.tools.dbmigrator.business.service.ErrorTrackingService;
import com.seguridata.tools.dbmigrator.data.entity.ErrorTrackingEntity;
import com.seguridata.tools.dbmigrator.data.entity.PlanEntity;
import com.seguridata.tools.dbmigrator.data.entity.ProjectEntity;
import com.seguridata.tools.dbmigrator.data.entity.TableEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class PlanExecutionCallable implements Callable<String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlanExecutionCallable.class);

    private final ProjectEntity project;
    private final PlanEntity plan;
    private final DatabaseQueryManager sourceQueryManager;
    private final DatabaseQueryManager targetQueryManager;
    private final ErrorTrackingService errorTrackingService;

    private CountDownLatch latch;

    public PlanExecutionCallable(ProjectEntity project,
                                 PlanEntity plan,
                                 DatabaseQueryManager sourceQueryManager,
                                 DatabaseQueryManager targetQueryManager,
                                 ErrorTrackingService errorTrackingService) {
        this.project = project;
        this.plan = plan;
        this.sourceQueryManager = sourceQueryManager;
        this.targetQueryManager = targetQueryManager;
        this.errorTrackingService = errorTrackingService;

        this.latch = null;
    }

    public void initialize(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public String call() {
        LOGGER.info("Executing Task: {}", Thread.currentThread().getName());
        if (Objects.isNull(this.latch)) {
            LOGGER.error("Latch is null");
            throw new IllegalStateException("Task should be initialized with a latch");
        }

        try {
            // Retrieve Data from SourceTable
            List<Map<String, Object>> sourceData = this.getSourceData();

            // Process the data using the function defined in "Definitions"

            // Insert the data to TargetTable
            TableEntity targetTable = this.plan.getTargetTable();
            for (Map<String, Object> resultSet : sourceData) {
                try {
                    this.targetQueryManager.insertDataBlockTo(targetTable, this.plan.getDefinitions(), resultSet);
                } catch (Exception e) {
                    ErrorTrackingEntity errorTracking = new ErrorTrackingEntity();
                    errorTracking.setMessage(e.getMessage());
                    errorTracking.setReferenceType("ResultSet");
                    errorTracking.setReferenceId(targetTable.getId()); // TODO: calculate the ID of the result set
                    this.errorTrackingService.createErrorTrackingForProject(this.project, errorTracking);
                }
            }

        } catch (Exception e) {
            LOGGER.error("Exception occurred on Task: {}", e.getMessage());
            ErrorTrackingEntity errorTracking = new ErrorTrackingEntity();
            errorTracking.setMessage(e.getMessage());
            errorTracking.setReferenceType(e.getClass().getCanonicalName());
            this.errorTrackingService.createErrorTrackingForProject(this.project, errorTracking);
        } finally {
            this.latch.countDown();
        }
        return "YES";
    }

    private List<Map<String, Object>> getSourceData() {
        LOGGER.info("Retrieving data from Source Table");
        TableEntity sourceTable = this.plan.getSourceTable();
        List<Map<String, Object>> resultList = this.sourceQueryManager
                .retrieveDataBlockFrom(sourceTable, this.plan.getDefinitions());

        if (CollectionUtils.isEmpty(resultList)) {
            ErrorTrackingEntity errorTracking = new ErrorTrackingEntity();
            errorTracking.setMessage("Source Data is Empty");
            errorTracking.setReferenceType(TableEntity.class.getCanonicalName());
            errorTracking.setReferenceId(sourceTable.getId());
            this.errorTrackingService.createErrorTrackingForProject(this.project, errorTracking);
            throw new EmptyResultException(errorTracking.getMessage());
        }

        return resultList;
    }
}
