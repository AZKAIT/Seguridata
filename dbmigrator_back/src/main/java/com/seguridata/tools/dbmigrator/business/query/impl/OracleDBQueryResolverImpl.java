package com.seguridata.tools.dbmigrator.business.query.impl;

import com.seguridata.tools.dbmigrator.business.query.DBQueryResolver;
import com.seguridata.tools.dbmigrator.business.query.annotation.DatabaseTypeBean;
import com.seguridata.tools.dbmigrator.data.constant.DatabaseType;
import com.seguridata.tools.dbmigrator.data.entity.DefinitionEntity;
import com.seguridata.tools.dbmigrator.data.entity.TableEntity;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@DatabaseTypeBean(dbType = DatabaseType.ORACLE)
public class OracleDBQueryResolverImpl implements DBQueryResolver {
    @Override
    public String getTablesQuery() {
        return "SELECT TABLE_NAME name, TABLESPACE_NAME schema FROM all_tables WHERE TABLESPACE_NAME = :namespace";
    }

    public String getColumnsQuery() {
        return "SELECT col.TABLE_NAME tableName, col.COLUMN_NAME name, col.DATA_TYPE dataType, col.DATA_LENGTH dataLength" +
                " FROM USER_TAB_COLS col, ALL_TABLES tab" +
                " WHERE col.TABLE_NAME = tab.TABLE_NAME AND col.TABLE_NAME = :tableName AND tab.TABLESPACE_NAME = :schema";
    }

    @Override
    public String verificationQuery() {
        return "SELECT 1 from dual";
    }


    @Override
    public String selectFromSourceTableQuery(TableEntity table, Collection<DefinitionEntity> definitions, long skip, long limit) {
        String columns = definitions.stream()
                .map(DefinitionEntity::getSourceColumn)
                .map(column -> String.format("SourceTable.%s", column.getName()))
                .collect(Collectors.joining(","));

        return "SELECT " + columns +" FROM " + this.paginationSource(table.getName(), table.getOrderColumnName(), skip, limit)
                + ", " + table.getName() + " SourceTable"
                + " WHERE RN > " + skip + " AND Pagination.RD = SourceTable.ROWID";
    }

    @Override
    public String insertToTargetTableQuery(TableEntity table, Collection<DefinitionEntity> definitions) {
        Set<String> colDefList = new LinkedHashSet<>();
        Set<String> paramDefList = new LinkedHashSet<>();

        definitions.forEach(definition -> {
            String columnName = definition.getTargetColumn().getName();
            colDefList.add(columnName);
            paramDefList.add(String.format(":%s", columnName));
        });

        String colDef = String.join(",", colDefList);
        String paramDef = String.join(",", paramDefList);

        return "INSERT INTO " + table.getName() + " (" + colDef + ") values (" + paramDef + ")";
    }

    @Override
    public String countQuery() {
        return "SELECT COUNT(*) \"Total\" FROM :schema.:tableName";
    }

    private String paginationSource(String tableName, String orderColumn, long skip, long limit) {
        return "(SELECT ROWNUM RN, RD FROM (SELECT ROWID RD FROM " + tableName + " ORDER BY " + tableName +"." + orderColumn + ") WHERE ROWNUM <= " + (skip + limit) + ") Pagination";
    }
}
