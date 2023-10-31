package com.seguridata.tools.dbmigrator.business.facade;

import com.seguridata.tools.dbmigrator.business.client.StompMessageClient;
import com.seguridata.tools.dbmigrator.business.exception.BaseCodeException;
import com.seguridata.tools.dbmigrator.business.exception.EmptyResultException;
import com.seguridata.tools.dbmigrator.business.mapper.ProjectMapper;
import com.seguridata.tools.dbmigrator.business.service.ColumnService;
import com.seguridata.tools.dbmigrator.business.service.DefinitionService;
import com.seguridata.tools.dbmigrator.business.service.PlanService;
import com.seguridata.tools.dbmigrator.business.service.TableService;
import com.seguridata.tools.dbmigrator.data.entity.ColumnEntity;
import com.seguridata.tools.dbmigrator.data.entity.ConnectionEntity;
import com.seguridata.tools.dbmigrator.data.entity.DefinitionEntity;
import com.seguridata.tools.dbmigrator.data.entity.PlanEntity;
import com.seguridata.tools.dbmigrator.data.entity.ProjectEntity;
import com.seguridata.tools.dbmigrator.data.entity.TableEntity;
import com.seguridata.tools.dbmigrator.data.model.ProjectModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class ProjectSyncUpFacade {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectSyncUpFacade.class);

    private final ProjectMapper projectMapper;
    private final TableService tableService;
    private final ColumnService columnService;
    private final PlanService planService;
    private final DefinitionService definitionService;
    private final StompMessageClient stompMsgClient;

    @Autowired
    public ProjectSyncUpFacade(ProjectMapper projectMapper,
                               TableService tableService,
                               ColumnService columnService,
                               PlanService planService,
                               DefinitionService definitionService,
                               StompMessageClient stompMsgClient) {
        this.projectMapper = projectMapper;
        this.tableService = tableService;
        this.columnService = columnService;
        this.planService = planService;
        this.definitionService = definitionService;
        this.stompMsgClient = stompMsgClient;
    }

    public void syncUpProjectPlans(ProjectModel projectModel) {
        ProjectEntity project = null;
        try {
            project = this.projectMapper.mapProjectEntity(projectModel);
            this.stompMsgClient.sendProjectSyncUpStatusChange(project, "INICIADO - Creación de Planes y Definiciones");

            List<TableEntity> sourceTables = this.getConnectionTables(project.getSourceConnection());
            List<TableEntity> targetTables = this.getConnectionTables(project.getTargetConnection());
            if (CollectionUtils.isEmpty(sourceTables) || CollectionUtils.isEmpty(targetTables)) {
                throw new EmptyResultException("La Conexión Origen o Destino no tiene Tablas presentes");
            }

            List<PlanEntity> createdPlans = this.createPlansForTables(project, sourceTables, targetTables);
            if (CollectionUtils.isEmpty(createdPlans)) {
                throw new EmptyResultException("No se pudieron crear los Planes nuevos");
            }

            List<DefinitionEntity> newDefs = createdPlans.stream()
                    .map(this::createDefinitionsForPlans)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(newDefs)) {
                throw new EmptyResultException("No se pudieron crear las Definiciones para los Planes");
            }
            this.stompMsgClient.sendProjectSyncUpStatusChange(project, "TERMINADO - Creación de Planes y Definiciones");
        } catch (BaseCodeException e) {
            LOGGER.error("Error Syncing Up Plans / Definitions: {}", e.getMessage());
            this.stompMsgClient.sendProjectSyncUpError(project, String.format("ERROR - Creación de Planes y Definiciones: %s", String.join(", ", e.getMessages())));
        }
    }

    private List<TableEntity> getConnectionTables(ConnectionEntity connection) {
        if (Objects.nonNull(connection)) {
            return this.tableService.getTablesForConnection(connection.getId());
        }

        return Collections.emptyList();
    }

    private List<ColumnEntity> getTableColumn(TableEntity table) {
        if (Objects.nonNull(table)) {
            return this.columnService.getColumnsForTable(table.getId());
        }
        return Collections.emptyList();
    }

    private List<PlanEntity> createPlansForTables(ProjectEntity project, List<TableEntity> sourceTables, List<TableEntity> targetTables) {
        AtomicInteger orderNum = new AtomicInteger(0);
        List<PlanEntity> newPlans = sourceTables.stream()
                .map(table ->
                        this.findTableByName(table, targetTables)
                                .map(targetTable -> this.mapPlan(orderNum.incrementAndGet(), table, targetTable))
                                .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(newPlans)) {
            return this.planService.savePlanListForProject(project, newPlans);
        }
        return Collections.emptyList();
    }

    private List<DefinitionEntity> createDefinitionsForPlans(PlanEntity plan) {
        List<ColumnEntity> sourceColumns = this.getTableColumn(plan.getSourceTable());
        List<ColumnEntity> targetColumns = this.getTableColumn(plan.getTargetTable());

        List<DefinitionEntity> newDefinitions = sourceColumns.stream().map(sourceColumn ->
                this.findColumnByName(sourceColumn, targetColumns)
                        .map(targetColumn -> this.mapDefinition(sourceColumn, targetColumn))
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(newDefinitions)) {
            return this.definitionService.saveDefListForProject(plan, newDefinitions);
        }
        return Collections.emptyList();
    }

    private PlanEntity mapPlan(Integer orderNum, TableEntity sourceTable, TableEntity targetTable) {
        PlanEntity plan = new PlanEntity();
        plan.setOrderNum(orderNum);
        plan.setSourceTable(sourceTable);
        plan.setTargetTable(targetTable);
        plan.setInitialSkip(0L);
        plan.setRowLimit(500L);
        plan.setMaxRows(-1L);

        return plan;
    }

    private DefinitionEntity mapDefinition(ColumnEntity sourceColumn, ColumnEntity targetColumn) {
        DefinitionEntity definition = new DefinitionEntity();
        definition.setSourceColumn(sourceColumn);
        definition.setTargetColumn(targetColumn);

        return definition;
    }

    private Optional<TableEntity> findTableByName(TableEntity needle, List<TableEntity> hayStack) {
        return hayStack.stream().filter(table -> needle.getName().equalsIgnoreCase(table.getName())).findFirst();
    }

    private Optional<ColumnEntity> findColumnByName(ColumnEntity needle, List<ColumnEntity> hayStack) {
        return hayStack.stream().filter(column ->  needle.getName().equalsIgnoreCase(column.getName())).findFirst();
    }
}
