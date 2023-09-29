package com.seguridata.tools.dbmigrator.business.query.impl;

import com.seguridata.tools.dbmigrator.business.query.DBQueryResolver;
import com.seguridata.tools.dbmigrator.business.query.annotation.DatabaseTypeBean;
import com.seguridata.tools.dbmigrator.data.constant.DatabaseType;
import com.seguridata.tools.dbmigrator.data.entity.ColumnEntity;
import com.seguridata.tools.dbmigrator.data.entity.DefinitionEntity;
import com.seguridata.tools.dbmigrator.data.entity.TableEntity;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@DatabaseTypeBean(dbType = DatabaseType.ORACLE)
public class OracleDBQueryResolverImpl implements DBQueryResolver {
    @Override
    public String verificationQuery() {
        return "SELECT 1 from dual";
    }


    @Override
    public String selectFromSourceTableQuery(TableEntity table, Collection<DefinitionEntity> definitions) {
        String columns = definitions.stream()
                .map(DefinitionEntity::getSourceColumn)
                .map(ColumnEntity::getName)
                .collect(Collectors.joining(","));

        return "SELECT " + columns +" FROM " + table.getName() + ""; // TODO: limit and sort queries depending on business rules defined by SeguriData
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
}
