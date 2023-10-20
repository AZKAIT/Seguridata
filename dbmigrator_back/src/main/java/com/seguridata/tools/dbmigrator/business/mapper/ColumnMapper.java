package com.seguridata.tools.dbmigrator.business.mapper;

import com.seguridata.tools.dbmigrator.data.entity.ColumnEntity;
import com.seguridata.tools.dbmigrator.data.model.ColumnModel;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ColumnMapper {

    public List<ColumnEntity> mapColumnEntityList(Collection<ColumnModel> columnModels) {
        return columnModels.stream().filter(Objects::nonNull).map(this::mapColumnEntity).collect(Collectors.toList());
    }

    public List<ColumnModel> mapColumnModelList(Collection<ColumnEntity> columnEntities) {
        return columnEntities.stream().filter(Objects::nonNull).map(this::mapColumnModel).collect(Collectors.toList());
    }

    public ColumnEntity mapColumnEntity(ColumnModel columnModel) {
        if (Objects.isNull(columnModel)) {
            return null;
        }

        ColumnEntity column = new ColumnEntity();
        column.setId(columnModel.getId());
        column.setName(columnModel.getName());
        column.setDescription(columnModel.getDescription());
        column.setDataType(columnModel.getDataType());
        column.setDataLength(columnModel.getDataLength());

        return column;
    }

    public ColumnModel mapColumnModel(ColumnEntity column) {
        if (Objects.isNull(column)) {
            return null;
        }

        ColumnModel columnModel = new ColumnModel();
        columnModel.setId(column.getId());
        columnModel.setName(column.getName());
        columnModel.setDescription(column.getDescription());
        columnModel.setDataType(column.getDataType());
        columnModel.setDataLength(column.getDataLength());
        columnModel.setIdentity(column.getIdentity());

        return columnModel;
    }
}
