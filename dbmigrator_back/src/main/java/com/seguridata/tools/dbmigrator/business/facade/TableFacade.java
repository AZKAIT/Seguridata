package com.seguridata.tools.dbmigrator.business.facade;

import com.seguridata.tools.dbmigrator.business.exception.BaseCodeException;
import com.seguridata.tools.dbmigrator.business.exception.MissingObjectException;
import com.seguridata.tools.dbmigrator.business.exception.ObjectLockedException;
import com.seguridata.tools.dbmigrator.business.mapper.TableMapper;
import com.seguridata.tools.dbmigrator.business.service.ConnectionService;
import com.seguridata.tools.dbmigrator.business.service.TableService;
import com.seguridata.tools.dbmigrator.data.entity.ConnectionEntity;
import com.seguridata.tools.dbmigrator.data.entity.TableEntity;
import com.seguridata.tools.dbmigrator.data.model.TableModel;
import com.seguridata.tools.dbmigrator.data.wrapper.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.lang.Boolean.TRUE;

@Component
public class TableFacade {

    private final ConnectionService connectionService;
    private final TableMapper tableMapper;
    private final TableService tableService;

    @Autowired
    public TableFacade(ConnectionService connectionService,
                       TableMapper tableMapper,
                       TableService tableService) {
        this.connectionService = connectionService;
        this.tableMapper = tableMapper;
        this.tableService = tableService;
    }

    public ResponseWrapper<TableModel> createTable(String connectionId, TableModel tableModel) {
        ResponseWrapper<TableModel> tableResponse = new ResponseWrapper<>();

        try {
            ConnectionEntity connection = this.connectionService.getConnection(connectionId);
            this.connectionService.validateConnectionStatus(connection);

            TableEntity table = this.tableService.createNewTable(connection, this.tableMapper.mapTableEntity(tableModel));

            tableResponse.setCode("00");
            tableResponse.setData(this.tableMapper.mapTableModel(table));
        } catch (BaseCodeException e) {
            tableResponse.setCode(e.getCode());
            tableResponse.setMessages(Arrays.asList(e.getMessages()));
        }

        return tableResponse;
    }

    public ResponseWrapper<List<TableModel>> getTablesForConnection(String connectionId) {
        ResponseWrapper<List<TableModel>> tableResponse = new ResponseWrapper<>();

        try {
            List<TableEntity> tables = this.tableService.getTables(connectionId);
            tableResponse.setCode("00");
            tableResponse.setData(this.tableMapper.mapTableModelList(tables));
        } catch (BaseCodeException e) {
            tableResponse.setCode(e.getCode());
            tableResponse.setMessages(Arrays.asList(e.getMessages()));
        }

        return tableResponse;
    }

    public ResponseWrapper<TableModel> getTable(String tableId) {
        ResponseWrapper<TableModel> tableResponse = new ResponseWrapper<>();

        try {
            TableEntity tableEntity = this.tableService.getTable(tableId);

            tableResponse.setCode("00");
            tableResponse.setData(this.tableMapper.mapTableModel(tableEntity));
        } catch (BaseCodeException e) {
            tableResponse.setCode(e.getCode());
            tableResponse.setMessages(Arrays.asList(e.getMessages()));
        }

        return tableResponse;
    }

    public ResponseWrapper<TableModel> updateTable(String tableId, TableModel tableModel) {
        ResponseWrapper<TableModel> tableResponse = new ResponseWrapper<>();
        try {
            TableEntity existingTable = this.tableService.getTable(tableId);
            this.connectionService.validateConnectionStatus(existingTable.getConnection());

            TableEntity updatedTable = this.tableService.updateTable(existingTable, this.tableMapper.mapTableEntity(tableModel));

            tableResponse.setCode("00");
            tableResponse.setData(this.tableMapper.mapTableModel(updatedTable));
        } catch (BaseCodeException e) {
            tableResponse.setCode(e.getCode());
            tableResponse.setMessages(Arrays.asList(e.getMessages()));
        }

        return tableResponse;
    }
}
