package com.seguridata.tools.dbmigrator.business.facade;

import com.seguridata.tools.dbmigrator.business.client.StompMessageClient;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ConnectionSyncUpFacade {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionSyncUpFacade.class);

    private final ApplicationContext appContext;
    private final ConnectionMapper connectionMapper;
    private final ConnectionService connectionService;
    private final TableService tableService;
    private final TableMapper tableMapper;

    private final ColumnService columnService;
    private final ColumnMapper columnMapper;

    private final StompMessageClient stompMsgClient;

    @Autowired
    public ConnectionSyncUpFacade(ApplicationContext appContext,
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
        this.stompMsgClient = this.appContext.getBean(StompMessageClient.class);
    }

    public ResponseWrapper<List<TableModel>> syncUpConnectionTables(String connectionId) {
        ResponseWrapper<List<TableModel>> tablesResponse = new ResponseWrapper<>();
        try {
            ConnectionEntity entity = this.connectionService.getConnection(connectionId);
            this.connectionService.validateConnectionStatus(entity);
            List<TableModel> createdTableModels = this.syncUpConnectionTables(entity);

            tablesResponse.setCode("00");
            tablesResponse.setData(createdTableModels);
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
        ConnectionEntity entity = null;
        try {
            entity = this.connectionMapper.mapConnectionEntity(connectionModel);
            this.stompMsgClient.sendConnSyncUpStatusChange(entity, "INICIADO - Sincronización de Tablas");

            this.syncUpConnectionTables(entity);
            this.stompMsgClient.sendConnSyncUpStatusChange(entity, "TERMINADO - Sincronización de Tablas");
        } catch (SQLException e) {
            LOGGER.error("SyncUp failed: {}", e.getMessage());
            this.stompMsgClient.sendConnSyncUpError(entity, String.format("ERROR - Sincronización de Tablas: %s", e.getMessage()));
        }
    }

    public ResponseWrapper<List<ColumnModel>> syncUpConnectionColumns(String connectionId) {
        ResponseWrapper<List<ColumnModel>> columnsResponse = new ResponseWrapper<>();
        try {
            ConnectionEntity entity = this.connectionService.getConnection(connectionId);
            this.connectionService.validateConnectionStatus(entity);
            List<TableEntity> tables = this.tableService.getTablesForConnection(connectionId);

            DatabaseQueryManager queryManager = this.appContext.getBean(DatabaseQueryManager.class, this.appContext);
            queryManager.initializeConnection(entity);

            List<ColumnEntity> createdColumns = this.syncUpTableColumns(entity, queryManager, tables);
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

    public void syncUpAllTableColumns(ConnectionModel connectionModel, List<TableModel> tableModels) {
        ConnectionEntity entity = null;
        try {
            entity = this.connectionMapper.mapConnectionEntity(connectionModel);
            this.stompMsgClient.sendConnSyncUpStatusChange(entity, "INICIADO - Sincronización de Columnas");

            List<TableEntity> table = this.tableMapper.mapTableEntityList(tableModels);

            DatabaseQueryManager queryManager = this.appContext.getBean(DatabaseQueryManager.class, this.appContext);
            queryManager.initializeConnection(entity);
            this.syncUpTableColumns(entity, queryManager, table);
            queryManager.closeConnection();
            this.stompMsgClient.sendConnSyncUpStatusChange(entity, "TERMINADO - Sincronización de Columnas");
        } catch (SQLException e) {
            LOGGER.error("SyncUp failed: {}", e.getMessage());
            this.stompMsgClient.sendConnSyncUpError(entity, String.format("ERROR - Sincronización de Columnas: %s", e.getMessage()));
        } catch (BaseCodeException e) {
            this.stompMsgClient.sendConnSyncUpError(entity, String.format("ERROR - Sincronización de Columnas: %s", String.join(", ", e.getMessages())));
        }
    }

    private List<TableModel> syncUpConnectionTables(ConnectionEntity connection) throws SQLException {
        DatabaseQueryManager queryManager = this.appContext.getBean(DatabaseQueryManager.class, this.appContext);
        queryManager.initializeConnection(connection);

        List<TableEntity> schemaTables = queryManager.findSchemaTables();
        schemaTables = this.tableService.saveBatch(connection, schemaTables);
        queryManager.closeConnection();

        ConnectionModel connectionModel = this.connectionMapper.mapConnectionModel(connection);
        List<TableModel> tableModels = this.tableMapper.mapTableModelList(schemaTables);
        connectionModel.setPassword(connection.getPassword());
        this.appContext.publishEvent(new TableCreatedEvent(this, connectionModel, tableModels));

        return tableModels;
    }

    private List<ColumnEntity> syncUpTableColumns(ConnectionEntity connection, DatabaseQueryManager queryManager, List<TableEntity> tables) {
        return tables.stream().flatMap(table -> {
                    try {
                        List<ColumnEntity> tableColumns = queryManager.findColumnForTable(table);
                        return this.columnService.saveBatch(table, tableColumns).stream();
                    } catch (BaseCodeException e) {
                        this.stompMsgClient.sendConnSyncUpError(connection, String.format("ERROR - Sincronización de Columnas: %s", String.join(", ", e.getMessages())));
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
