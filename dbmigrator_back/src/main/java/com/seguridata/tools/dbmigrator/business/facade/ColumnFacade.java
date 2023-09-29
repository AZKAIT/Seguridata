package com.seguridata.tools.dbmigrator.business.facade;

import com.seguridata.tools.dbmigrator.business.exception.BaseCodeException;
import com.seguridata.tools.dbmigrator.business.exception.EmptyResultException;
import com.seguridata.tools.dbmigrator.business.exception.MissingObjectException;
import com.seguridata.tools.dbmigrator.business.exception.ObjectLockedException;
import com.seguridata.tools.dbmigrator.business.mapper.ColumnMapper;
import com.seguridata.tools.dbmigrator.business.service.ColumnService;
import com.seguridata.tools.dbmigrator.business.service.ConnectionService;
import com.seguridata.tools.dbmigrator.business.service.TableService;
import com.seguridata.tools.dbmigrator.data.entity.ColumnEntity;
import com.seguridata.tools.dbmigrator.data.entity.ConnectionEntity;
import com.seguridata.tools.dbmigrator.data.entity.TableEntity;
import com.seguridata.tools.dbmigrator.data.model.ColumnModel;
import com.seguridata.tools.dbmigrator.data.wrapper.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.lang.Boolean.TRUE;

@Component
public class ColumnFacade {

    private final ConnectionService connectionService;
    private final TableService tableService;
    private final ColumnService columnService;
    private final ColumnMapper columnMapper;

    @Autowired
    public ColumnFacade(ConnectionService connectionService,
                        TableService tableService,
                        ColumnService columnService,
                        ColumnMapper columnMapper) {
        this.connectionService = connectionService;
        this.tableService = tableService;
        this.columnService = columnService;
        this.columnMapper = columnMapper;
    }

    public ResponseWrapper<ColumnModel> createColumn(String tableId, ColumnModel columnModel) {
        ResponseWrapper<ColumnModel> response = new ResponseWrapper<>();
        try {
            TableEntity table = this.tableService.getTable(tableId);
            this.connectionService.validateConnectionStatus(table.getConnection());

            ColumnEntity column = this.columnService.createColumn(table, this.columnMapper.mapColumnEntity(columnModel));

            response.setCode("00");
            response.setData(this.columnMapper.mapColumnModel(column));
        } catch (BaseCodeException e) {
            response.setCode(e.getCode());
            response.setMessages(Arrays.asList(e.getMessages()));
        }

        return response;
    }

    public ResponseWrapper<ColumnModel> getColumn(String columnId) {
        ResponseWrapper<ColumnModel> response = new ResponseWrapper<>();
        try {
            ColumnEntity column = this.columnService.getColumn(columnId);

            response.setCode("00");
            response.setData(this.columnMapper.mapColumnModel(column));
        } catch (BaseCodeException e) {
            response.setCode(e.getCode());
            response.setMessages(Arrays.asList(e.getMessages()));
        }

        return response;
    }

    public ResponseWrapper<List<ColumnModel>> getColumnsForTable(String tableId) {
        ResponseWrapper<List<ColumnModel>> response = new ResponseWrapper<>();

        try {
            List<ColumnEntity> columns = this.columnService.getColumnsForTable(tableId);
            response.setCode("00");
            response.setData(this.columnMapper.mapColumnModelList(columns));
        } catch (BaseCodeException e) {
            response.setCode(e.getCode());
            response.setMessages(Arrays.asList(e.getMessages()));
        }

        return response;
    }

    public ResponseWrapper<ColumnModel> updateColumn(String columnId, ColumnModel columnModel) {
        ResponseWrapper<ColumnModel> response = new ResponseWrapper<>();
        try {
            ColumnEntity existingColumn = this.columnService.getColumn(columnId);
            TableEntity table = existingColumn.getTable();
            if (Objects.isNull(table)) {
                throw new MissingObjectException("Table not found on column");
            }

            this.connectionService.validateConnectionStatus(table.getConnection());

            ColumnEntity column = this.columnService
                    .updateColumn(existingColumn, this.columnMapper.mapColumnEntity(columnModel));

            response.setCode("00");
            response.setData(this.columnMapper.mapColumnModel(column));
        } catch (BaseCodeException e) {
            response.setCode(e.getCode());
            response.setMessages(Arrays.asList(e.getMessages()));
        }

        return response;
    }

    public ResponseWrapper<List<ColumnModel>> createBatchOfColumns(String tableId, List<ColumnModel> columnModelList) {
        ResponseWrapper<List<ColumnModel>> response = new ResponseWrapper<>();
        try {
            TableEntity table = this.tableService.getTable(tableId);
            this.connectionService.validateConnectionStatus(table.getConnection());

            List<ColumnEntity> columns = this.columnMapper.mapColumnEntityList(columnModelList);
            if (CollectionUtils.isEmpty(columns)) {
                throw new EmptyResultException("Input Column list is empty");
            }
            columns = this.columnService.createColumnList(table, columns);

            response.setCode("00");
            response.setData(this.columnMapper.mapColumnModelList(columns));
        } catch (BaseCodeException e) {
            response.setCode(e.getCode());
            response.setMessages(Arrays.asList(e.getMessages()));
        }
        return response;
    }
}
