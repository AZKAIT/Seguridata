package com.seguridata.tools.dbmigrator.business.task;

import com.seguridata.tools.dbmigrator.business.exception.DBValidationException;
import com.seguridata.tools.dbmigrator.business.exception.EmptyResultException;
import com.seguridata.tools.dbmigrator.business.exception.MissingObjectException;
import com.seguridata.tools.dbmigrator.business.manager.DatabaseQueryManager;
import com.seguridata.tools.dbmigrator.business.service.ErrorTrackingService;
import com.seguridata.tools.dbmigrator.business.service.JobService;
import com.seguridata.tools.dbmigrator.data.constant.ConversionFunction;
import com.seguridata.tools.dbmigrator.data.constant.ExecutionResult;
import com.seguridata.tools.dbmigrator.data.constant.ExecutionStatus;
import com.seguridata.tools.dbmigrator.data.entity.DefinitionEntity;
import com.seguridata.tools.dbmigrator.data.entity.ErrorTrackingEntity;
import com.seguridata.tools.dbmigrator.data.entity.JobEntity;
import com.seguridata.tools.dbmigrator.data.entity.PlanEntity;
import com.seguridata.tools.dbmigrator.data.entity.TableEntity;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.util.CollectionUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class PlanExecutionCallable implements Callable<ExecutionResult> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlanExecutionCallable.class);

    private final JobEntity job;
    private final PlanEntity plan;
    private final DatabaseQueryManager sourceQueryManager;
    private final DatabaseQueryManager targetQueryManager;
    private final JobService jobService;
    private final ErrorTrackingService errorTrackingService;

    private CountDownLatch latch;

    public PlanExecutionCallable(JobEntity job,
                                 PlanEntity plan,
                                 DatabaseQueryManager sourceQueryManager,
                                 DatabaseQueryManager targetQueryManager,
                                 JobService jobService,
                                 ErrorTrackingService errorTrackingService) {
        this.job = job;
        this.plan = plan;
        this.sourceQueryManager = sourceQueryManager;
        this.targetQueryManager = targetQueryManager;
        this.jobService = jobService;
        this.errorTrackingService = errorTrackingService;

        this.latch = null;
    }

    public void initialize(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public ExecutionResult call() {
        TableEntity sourceTable = this.plan.getSourceTable();
        TableEntity targetTable = this.plan.getTargetTable();
        Thread.currentThread().setName(String.format("%s=>%s", sourceTable.getName(), targetTable.getName()));
        this.jobService.updateExecutionStatus(this.job.getId(), this.plan.getId(), ExecutionStatus.RUNNING);

        LOGGER.info("Executing Task: {}", Thread.currentThread().getName());
        if (Objects.isNull(this.latch)) {
            LOGGER.error("Latch is null");
            throw new IllegalStateException("La tarea se debe inicializar con un objeto Latch");
        }

        ExecutionResult executionResult = null;
        try {
            if (StringUtils.isBlank(sourceTable.getOrderColumnName())) {
                throw new MissingObjectException(String.format("No se ha configurado ordenamiento para %s", sourceTable.getName()));
            }

            List<DefinitionEntity> dataProcessDefinitions = this.plan.getDefinitions();
            if (CollectionUtils.isEmpty(dataProcessDefinitions)) {
                throw new EmptyResultException("La lista de Definiciones está vacía");
            }


            long totalRowsSource = this.sourceQueryManager.getTotalRows(sourceTable);
            if (totalRowsSource == 0) {
                throw new EmptyResultException("No Data for table " + sourceTable.getName());
            }

            final long rowLimit = this.plan.getRowLimit();
            final long maxRows = this.plan.getMaxRows();
            final long initialSkip = this.plan.getInitialSkip();

            if (initialSkip > totalRowsSource) {
                throw new IllegalStateException("El número de filas omitidas es mayor al número total de registros");
            }

            if (maxRows != -1 && rowLimit > maxRows) {
                throw new IllegalStateException("El tamaño de bloque es mayor a la cantidad máxima de registros a procesar");
            }

            long currentRows = 0;
            long skip = initialSkip;
            long rowsForCompletion = maxRows > 0 ? maxRows : totalRowsSource;
            LOGGER.info("Initiating process for {} rows in batches of {} starting from row {}, on a total of {} rows", maxRows, rowLimit, initialSkip, totalRowsSource);
            while (this.validateRowNum(currentRows, maxRows) && ((initialSkip + currentRows) < totalRowsSource) && !Thread.interrupted()) {
                // Retrieve Data from SourceTable
                List<Map<String, Object>> sourceData = this.getSourceData(sourceTable, skip, rowLimit);
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }

                // Process the data using the function defined in "Definitions"
                List<Map<String, Object>> insertData = this.processDataConversion(sourceData, dataProcessDefinitions);
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }

                // Insert Data into source table
                long insertedRows = this.insertTargetData(insertData, targetTable, dataProcessDefinitions);
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }

                currentRows += insertedRows;
                skip += insertedRows;

                LOGGER.info("Processed {} rows of ({} / {}) next skip is {} for {} => {}",
                        currentRows, totalRowsSource, maxRows, skip, sourceTable.getName(), targetTable.getName());
                this.jobService.updateExecutionProgress(this.job.getId(), this.plan.getId(), currentRows, rowsForCompletion);
            }

            executionResult = ExecutionResult.SUCCESS;
        } catch (InterruptedException e) {
            LOGGER.warn("Process was interrupted: {}", e.getMessage());
            executionResult = ExecutionResult.INTERRUPTED;
        } catch (Exception e) {
            executionResult = ExecutionResult.EXCEPTION;
            LOGGER.error("Exception occurred on Task: {}", getStackTrace(e));
            ErrorTrackingEntity errorTracking = new ErrorTrackingEntity();
            errorTracking.setMessage(e.getMessage());
            errorTracking.setReferenceType(PlanEntity.class.getCanonicalName());
            errorTracking.setReferenceId(this.plan.getId());
            this.errorTrackingService.createErrorTrackingForProject(this.job, errorTracking);
        } finally {
            LOGGER.info("Terminating Task: {}", Thread.currentThread().getName());
            this.latch.countDown();
            this.jobService.updateExecutionResult(this.job.getId(), this.plan.getId(), executionResult);
        }
        return executionResult;
    }

    private String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);

        return sw.toString();
    }

    private List<Map<String, Object>> getSourceData(TableEntity table, long skip, long limit) {
        List<Map<String, Object>> resultList = this.sourceQueryManager
                .retrieveDataBlockFrom(table, this.plan.getDefinitions(), skip, limit);

        if (CollectionUtils.isEmpty(resultList)) {
            throw new EmptyResultException("Source Data is Empty");
        }

        return resultList;
    }

    private long insertTargetData(List<Map<String, Object>> sourceData, TableEntity table, List<DefinitionEntity> dataProcessDefinitions) throws InterruptedException {
        long insertedRows = 0;
        for (Map<String, Object> resultSet : sourceData) {
            try {
                insertedRows += this.targetQueryManager.insertDataBlockTo(table, dataProcessDefinitions, resultSet);
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
            } catch (DuplicateKeyException e) {
                LOGGER.warn("Duplicate Key found, ignoring: {}", e.getMessage());
                insertedRows++;
            } catch(InterruptedException e) {
                throw e;
            } catch (Exception e) {
                LOGGER.error("Unexpected error: {}", e.getMessage());
                throw new DBValidationException(e.getMessage());
            }
        }

        LOGGER.debug("Inserted rows after execution: {}", insertedRows);
        return insertedRows;
    }

    private List<Map<String, Object>> processDataConversion(List<Map<String, Object>> sourceData,
                                                            List<DefinitionEntity> dataProcessDefinitions) {
        return sourceData.stream()
                .map(rowData -> this.mapDataDefinitions(rowData, dataProcessDefinitions))
                .collect(Collectors.toList());
    }

    private boolean validateRowNum(long currentRows, long maxRows) {
        return maxRows == -1 || currentRows < maxRows;
    }

    private Map<String, Object> mapDataDefinitions(Map<String, Object> row, List<DefinitionEntity> dataProcessDefinitions) {
        return dataProcessDefinitions.stream()
                .map(definition -> {
                    Object sourceValue = row.get(definition.getSourceColumn().getName());
                    if (Objects.isNull(sourceValue)) {
                        return null;
                    }

                    if (!ConversionFunction.NONE.equals(definition.getConversionFunction())) {
                        // TODO: implement conversion functions
                    }
                    return new AbstractMap.SimpleEntry<>(definition.getTargetColumn().getName(), sourceValue);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }
}
