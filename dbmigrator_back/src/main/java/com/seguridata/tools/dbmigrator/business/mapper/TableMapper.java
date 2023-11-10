package com.seguridata.tools.dbmigrator.business.mapper;

import com.seguridata.tools.dbmigrator.data.entity.TableEntity;
import com.seguridata.tools.dbmigrator.data.model.TableModel;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class TableMapper {

    public List<TableEntity> mapTableEntityList(Collection<TableModel> tables) {
        return tables.stream().filter(Objects::nonNull).map(this::mapTableEntity).collect(Collectors.toList());
    }

    public List<TableModel> mapTableModelList(Collection<TableEntity> tableModels) {
        return tableModels.stream().filter(Objects::nonNull).map(this::mapTableModel).collect(Collectors.toList());
    }

    public TableEntity mapTableEntity(TableModel tableModel) {
        if (Objects.isNull(tableModel)) {
            return null;
        }

        TableEntity table = new TableEntity();
        table.setId(tableModel.getId());
        table.setName(tableModel.getName());
        table.setSchema(tableModel.getSchema());
        table.setDescription(tableModel.getDescription());
        table.setOrderColumnName(tableModel.getOrderColumnName());

        return table;
    }

    public TableModel mapTableModel(TableEntity table) {
        if (Objects.isNull(table)) {
            return null;
        }

        TableModel tableModel = new TableModel();
        tableModel.setId(table.getId());
        tableModel.setName(table.getName());
        tableModel.setSchema(table.getSchema());
        tableModel.setDescription(table.getDescription());
        tableModel.setOrderColumnName(table.getOrderColumnName());

        return tableModel;
    }
}
