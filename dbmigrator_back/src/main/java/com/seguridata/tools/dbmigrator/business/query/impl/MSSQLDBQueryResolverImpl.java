package com.seguridata.tools.dbmigrator.business.query.impl;

import com.seguridata.tools.dbmigrator.business.query.DBQueryResolver;
import com.seguridata.tools.dbmigrator.business.query.annotation.DatabaseTypeBean;
import com.seguridata.tools.dbmigrator.data.constant.DatabaseType;
import com.seguridata.tools.dbmigrator.data.entity.ColumnEntity;
import com.seguridata.tools.dbmigrator.data.entity.DefinitionEntity;
import com.seguridata.tools.dbmigrator.data.entity.TableEntity;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@DatabaseTypeBean(dbType = DatabaseType.MSSQL)
public class MSSQLDBQueryResolverImpl implements DBQueryResolver {
    @Override
    public String getTablesQuery() {
        return "SELECT TABLE_NAME AS 'name', TABLE_SCHEMA AS 'schema'" +
                " FROM INFORMATION_SCHEMA.TABLES" +
                " WHERE TABLE_TYPE = 'BASE TABLE' AND TABLE_CATALOG = :namespace";
    }

    @Override
    public String getColumnsQuery() {
        return "SELECT TABLE_NAME AS 'tableName', COLUMN_NAME AS 'name', DATA_TYPE AS 'dataType', CHARACTER_MAXIMUM_LENGTH AS 'dataLength', columnproperty(object_id(TABLE_NAME),COLUMN_NAME,'IsIdentity') AS 'isIdentity'" +
                " FROM INFORMATION_SCHEMA.COLUMNS" +
                " WHERE TABLE_NAME = :tableName AND TABLE_SCHEMA = :schema AND TABLE_CATALOG = :namespace";
    }

    @Override
    public String verificationQuery() {
        return "SELECT 1";
    }

    @Override
    public String selectFromSourceTableQuery(TableEntity table, Collection<DefinitionEntity> definitions, long skip, long limit) {
        String schemaPart = this.getSchemePart(table);

        String columns = definitions.stream()
                .map(DefinitionEntity::getSourceColumn)
                .map(ColumnEntity::getName)
                .collect(Collectors.joining(","));

        return "SELECT " + columns + " FROM " + schemaPart + table.getName()
                + " ORDER BY " + table.getOrderColumnName()
                + " OFFSET " + skip + " ROWS"
                + " FETCH NEXT " + limit + " ROWS ONLY";
    }

    @Override
    public String insertToTargetTableQuery(TableEntity table, Collection<DefinitionEntity> definitions) {
        String schemaPart = this.getSchemePart(table);

        Set<String> colDefList = new LinkedHashSet<>();
        Set<String> paramDefList = new LinkedHashSet<>();

        definitions.forEach(definition -> {
            String columnName = definition.getTargetColumn().getName();
            colDefList.add(columnName);
            paramDefList.add(String.format(":%s", columnName));
        });

        String colDef = String.join(",", colDefList);
        String paramDef = String.join(",", paramDefList);

        return "INSERT INTO " + schemaPart + table.getName() + " (" + colDef + ") values (" + paramDef + ")";
    }

    @Override
    public String countQuery(String schemaTableName) {
        return "SELECT COUNT(*) AS Total FROM " + schemaTableName;
    }


    public String identityInsertToggleQuery(TableEntity table, boolean status) {
        String statusText = "OFF";
        if (status) {
            statusText = "ON";
        }
        return String.format("SET IDENTITY_INSERT %s.%s %s", table.getSchema(), table.getName(), statusText);
    }


    private String getSchemePart(TableEntity table) {
        return Optional.ofNullable(table.getSchema())
                .filter(StringUtils::isNotBlank)
                .map(schema -> schema + ".")
                .orElse(StringUtils.EMPTY);
    }
}
