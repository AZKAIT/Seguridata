package com.seguridata.tools.dbmigrator.business.service;

import com.seguridata.tools.dbmigrator.business.exception.DuplicateDataException;
import com.seguridata.tools.dbmigrator.business.exception.EmptyResultException;
import com.seguridata.tools.dbmigrator.business.exception.MissingObjectException;
import com.seguridata.tools.dbmigrator.data.entity.ColumnEntity;
import com.seguridata.tools.dbmigrator.data.entity.TableEntity;
import com.seguridata.tools.dbmigrator.data.repository.ColumnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ColumnService {

    private final ColumnRepository columnRepo;

    @Autowired
    public ColumnService(ColumnRepository columnRepo) {
        this.columnRepo = columnRepo;
    }

    public ColumnEntity createColumn(TableEntity table, ColumnEntity column) {
        column.setId(null);
        column.setTable(table);
        if (Objects.isNull(column.getSorting())) {
            column.setSorting(false);
        }

        if (this.columnRepo.validateColumnData(table.getId(), column)) {
            throw new DuplicateDataException("Column already exists for given table");
        }

        return this.columnRepo.createColumn(column);
    }

    public List<ColumnEntity> getColumnsForTable(String tableId) {
        List<ColumnEntity> columns = this.columnRepo.findColumnListByTable(tableId);
        if (CollectionUtils.isEmpty(columns)) {
            throw new EmptyResultException("Empty column list for table");
        }

        return columns;
    }

    public ColumnEntity getColumn(String columnId) {
        ColumnEntity columnEntity = this.columnRepo.findColumn(columnId);

        if (Objects.isNull(columnEntity)) {
            throw new MissingObjectException("Column doesn't exist");
        }

        return columnEntity;
    }

    public ColumnEntity updateColumn(ColumnEntity existingColumn, ColumnEntity updatedColumn) {
        updatedColumn.setId(existingColumn.getId());
        updatedColumn.setTable(existingColumn.getTable());

        return this.columnRepo.updateColumn(updatedColumn);
    }

    public List<ColumnEntity> createColumnList(TableEntity table, List<ColumnEntity> columns) {
        columns.forEach(col -> {
            col.setId(null);
            col.setTable(table);
            if (Objects.isNull(col.getSorting())) {
                col.setSorting(false);
            }
        });

        return this.columnRepo.createColumnList(columns);
    }

    public ColumnEntity findSortingColumnForTable(String tableId) {
        List<ColumnEntity> sortingColumns = this.columnRepo.findSortingColumnForTable(tableId);
        if (CollectionUtils.isEmpty(sortingColumns)) {
            return null;
        }

        return sortingColumns.get(0);
    }

    public List<ColumnEntity> saveBatch(TableEntity table, List<ColumnEntity> columns) {
        return columns.stream().map(column -> {
                    try {
                        return this.createColumn(table, column);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
