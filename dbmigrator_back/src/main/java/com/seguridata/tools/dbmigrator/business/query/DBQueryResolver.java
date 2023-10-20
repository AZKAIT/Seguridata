package com.seguridata.tools.dbmigrator.business.query;

import com.seguridata.tools.dbmigrator.data.entity.DefinitionEntity;
import com.seguridata.tools.dbmigrator.data.entity.TableEntity;

import java.util.Collection;

public interface DBQueryResolver {
    String getTablesQuery();
    String getColumnsQuery();
    String verificationQuery();
    String selectFromSourceTableQuery(TableEntity table, Collection<DefinitionEntity> definitions, long skip, long limit);
    String insertToTargetTableQuery(TableEntity table, Collection<DefinitionEntity> definitions);
    String countQuery(String schemaTableName);
    String identityInsertToggleQuery(TableEntity table, boolean status);
}
