package com.seguridata.tools.dbmigrator.business.service;

import com.seguridata.tools.dbmigrator.business.exception.DuplicateDataException;
import com.seguridata.tools.dbmigrator.business.exception.EmptyResultException;
import com.seguridata.tools.dbmigrator.business.exception.MissingObjectException;
import com.seguridata.tools.dbmigrator.data.entity.ConnectionEntity;
import com.seguridata.tools.dbmigrator.data.entity.TableEntity;
import com.seguridata.tools.dbmigrator.data.repository.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TableService {
    private final TableRepository tableRepo;

    @Autowired
    public TableService(TableRepository tableRepo) {
        this.tableRepo = tableRepo;
    }

    public TableEntity createNewTable(ConnectionEntity connection, TableEntity table) {
        table.setId(null);
        table.setConnection(connection);

        if (this.tableRepo.validateTableData(connection.getId(), table)) {
            throw new DuplicateDataException("Table already exists for given connection");
        }

        return this.tableRepo.createTable(table);
    }

    public List<TableEntity> getTablesForConnection(String connectionId) {
        List<TableEntity> tables = this.tableRepo.getTableListByConnection(connectionId);
        if (CollectionUtils.isEmpty(tables)) {
            throw new EmptyResultException("Empty tables for connection");
        }

        return tables;
    }

    public TableEntity getTable(String tableId) {
        TableEntity tableEntity = this.tableRepo.getTable(tableId);

        if (Objects.isNull(tableEntity)) {
            throw new MissingObjectException("Table doesn't exist");
        }

        return tableEntity;
    }

    public TableEntity updateTable(TableEntity existingTable, TableEntity updatedTable) {
        updatedTable.setId(existingTable.getId());
        updatedTable.setConnection(existingTable.getConnection());

        return this.tableRepo.updateTable(updatedTable);
    }

    public void validateTableOwner(String connectionId, String tableId) {
        if (!this.tableRepo.validateTableConnection(connectionId, tableId)) {
            throw new RuntimeException("Table doesn't belong to Connection");
        }
    }

    public List<TableEntity> saveBatch(ConnectionEntity connection, List<TableEntity> tables) {
        return tables.stream().map(table -> {
                    try {
                        return this.createNewTable(connection, table);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public TableEntity deleteTable(TableEntity table) {
        return this.tableRepo.deleteTable(table.getId());
    }

    public List<TableEntity> deleteTablesForConn(ConnectionEntity connection) {
        return this.tableRepo.deleteTablesByConnection(Collections.singleton(connection.getId()));
    }
}
