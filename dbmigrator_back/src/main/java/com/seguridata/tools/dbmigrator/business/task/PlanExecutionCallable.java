package com.seguridata.tools.dbmigrator.business.task;

import com.seguridata.tools.dbmigrator.business.manager.DatabaseQueryManager;
import com.seguridata.tools.dbmigrator.business.service.ErrorTrackingService;
import com.seguridata.tools.dbmigrator.data.entity.PlanEntity;
import com.seguridata.tools.dbmigrator.data.entity.TableEntity;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class PlanExecutionCallable implements Callable<String> {

    private final PlanEntity plan;
    private final DatabaseQueryManager sourceQueryManager;
    private final DatabaseQueryManager targetQueryManager;
    private final ErrorTrackingService errorTrackingService;

    public PlanExecutionCallable(PlanEntity plan,
                                 DatabaseQueryManager sourceQueryManager,
                                 DatabaseQueryManager targetQueryManager,
                                 ErrorTrackingService errorTrackingService) {
        this.plan = plan;
        this.sourceQueryManager = sourceQueryManager;
        this.targetQueryManager = targetQueryManager;
        this.errorTrackingService = errorTrackingService;
    }


    @Override
    public String call() throws Exception {
        // Retrieve Data from SourceTable
        TableEntity sourceTable = this.plan.getSourceTable();
        List<Map<String, Object>> resultList = this.sourceQueryManager
                .retrieveDataBlockFrom(sourceTable, this.plan.getDefinitions());

        // Process the data using the function defined in "Definitions"

        // Insert the data to TargetTable
        TableEntity targetTable = this.plan.getTargetTable();
        //this.targetQueryManager.insertDataBlockTo(targetTable, this.plan.getDefinitions(), results);

        return "YES";
    }
}
