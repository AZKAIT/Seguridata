package com.seguridata.tools.dbmigrator.business.facade;

import com.seguridata.tools.dbmigrator.business.event.TableCreatedEvent;
import com.seguridata.tools.dbmigrator.business.exception.BaseCodeException;
import com.seguridata.tools.dbmigrator.business.manager.DatabaseQueryManager;
import com.seguridata.tools.dbmigrator.business.mapper.ColumnMapper;
import com.seguridata.tools.dbmigrator.business.mapper.ConnectionMapper;
import com.seguridata.tools.dbmigrator.business.mapper.TableMapper;
import com.seguridata.tools.dbmigrator.business.service.ColumnService;
import com.seguridata.tools.dbmigrator.business.service.ConnectionService;
import com.seguridata.tools.dbmigrator.business.service.TableService;
import com.seguridata.tools.dbmigrator.data.entity.ColumnEntity;
import com.seguridata.tools.dbmigrator.data.entity.ConnectionEntity;
import com.seguridata.tools.dbmigrator.data.entity.TableEntity;
import com.seguridata.tools.dbmigrator.data.model.ColumnModel;
import com.seguridata.tools.dbmigrator.data.model.ConnectionModel;
import com.seguridata.tools.dbmigrator.data.model.TableModel;
import com.seguridata.tools.dbmigrator.data.wrapper.ResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class SyncUpFacade {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncUpFacade.class);

    private final ApplicationContext appContext;
    private final ConnectionMapper connectionMapper;
    private final ConnectionService connectionService;
    private final TableService tableService;
    private final TableMapper tableMapper;

    private final ColumnService columnService;
    private final ColumnMapper columnMapper;

    @Autowired
    public SyncUpFacade(ApplicationContext appContext,
                        ConnectionMapper connectionMapper,
                        ConnectionService connectionService,
                        TableService tableService,
                        TableMapper tableMapper,

                        ColumnService columnService,
                        ColumnMapper columnMapper) {
        this.appContext = appContext;
        this.connectionMapper = connectionMapper;
        this.connectionService = connectionService;
        this.tableService = tableService;
        this.tableMapper = tableMapper;

        this.columnService = columnService;
        this.columnMapper = columnMapper;
    }

    public ResponseWrapper<List<TableModel>> syncUpConnectionTables(String connectionId) {
        ResponseWrapper<List<TableModel>> tablesResponse = new ResponseWrapper<>();
        try {
            ConnectionEntity entity = this.connectionService.getConnection(connectionId);
            List<TableEntity> createdTables = this.syncUpConnectionTables(entity);

            List<TableModel> createdTableModels = this.tableMapper.mapTableModelList(createdTables);

            tablesResponse.setCode("00");
            tablesResponse.setData(createdTableModels);

            ConnectionModel connectionModel = this.connectionMapper.mapConnectionModel(entity);
            createdTableModels.forEach(tableModel ->
                    this.appContext.publishEvent(new TableCreatedEvent(this, connectionModel, tableModel))
            );
        } catch (BaseCodeException e) {
            tablesResponse.setCode(e.getCode());
            tablesResponse.setMessages(Arrays.asList(e.getMessages()));
        } catch (SQLException e) {
            tablesResponse.setCode("08");
            tablesResponse.setMessages(Collections.singleton(e.getMessage()));
        }

        return tablesResponse;
    }

    public void syncUpConnectionTables(ConnectionModel connectionModel) {
        try {
            ConnectionEntity entity = this.connectionMapper.mapConnectionEntity(connectionModel);
            this.syncUpConnectionTables(entity);
        } catch (SQLException e) {
            LOGGER.error("SyncUp failed: {}", e.getMessage());
        }
    }

    public ResponseWrapper<List<ColumnModel>> syncUpConnectionColumns(String connectionId) {
        ResponseWrapper<List<ColumnModel>> columnsResponse = new ResponseWrapper<>();
        try {
            ConnectionEntity entity = this.connectionService.getConnection(connectionId);
            List<TableEntity> tables = this.tableService.getTablesForConnection(connectionId);

            DatabaseQueryManager queryManager = this.appContext.getBean(DatabaseQueryManager.class, this.appContext);
            queryManager.initializeConnection(entity);
            List<ColumnEntity> createdColumns = tables.stream().map(table ->
                this.syncUpTableColumns(queryManager, table)).flatMap(List::stream)
                    .collect(Collectors.toList());
            queryManager.closeConnection();

            columnsResponse.setCode("00");
            columnsResponse.setData(this.columnMapper.mapColumnModelList(createdColumns));
        } catch (BaseCodeException e) {
            columnsResponse.setCode(e.getCode());
            columnsResponse.setMessages(Arrays.asList(e.getMessages()));
        } catch (SQLException e) {
            columnsResponse.setCode("08");
            columnsResponse.setMessages(Collections.singleton(e.getMessage()));
        }

        return columnsResponse;
    }

    public void syncUpSingleTableColumn(ConnectionModel connectionModel, TableModel tableModel) {
        try {
            ConnectionEntity entity = this.connectionMapper.mapConnectionEntity(connectionModel);
            TableEntity table = this.tableMapper.mapTableEntity(tableModel);

            DatabaseQueryManager queryManager = this.appContext.getBean(DatabaseQueryManager.class, this.appContext);
            queryManager.initializeConnection(entity);
            this.syncUpTableColumns(queryManager, table);
            queryManager.closeConnection();
        } catch (SQLException e) {
            LOGGER.error("SyncUp failed: {}", e.getMessage());
        }
    }

    private List<TableEntity> syncUpConnectionTables(ConnectionEntity connection) throws SQLException {
        DatabaseQueryManager queryManager = this.appContext.getBean(DatabaseQueryManager.class, this.appContext);
        queryManager.initializeConnection(connection);

        List<TableEntity> schemaTables = queryManager.findSchemaTables();
        schemaTables = this.tableService.saveBatch(connection, schemaTables);
        queryManager.closeConnection();

        return schemaTables;
    }

    private List<ColumnEntity> syncUpTableColumns(DatabaseQueryManager queryManager, TableEntity table) {
        List<ColumnEntity> tableColumns = queryManager.findColumnForTable(table);
        tableColumns = this.columnService.saveBatch(table, tableColumns);

        return tableColumns;
    }
}
