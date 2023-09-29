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
    public String verificationQuery() {
        return "SELECT 1";
    }

    @Override
    public String selectFromSourceTableQuery(TableEntity table, Collection<DefinitionEntity> definitions) {
        String schemaPart = this.getSchemePart(table);

        String columns = definitions.stream()
                .map(DefinitionEntity::getSourceColumn)
                .map(ColumnEntity::getName)
                .collect(Collectors.joining(","));

        return "SELECT " + columns + " FROM " + schemaPart + table.getName() + ""; // TODO: limit and sort queries depending on business rules defined by SeguriData
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


    private String getSchemePart(TableEntity table) {
        return Optional.ofNullable(table.getSchema())
                .filter(StringUtils::isNotBlank)
                .map(schema -> schema + ".")
                .orElse(StringUtils.EMPTY);
    }
}
