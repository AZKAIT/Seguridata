package com.seguridata.tools.dbmigrator.business.query;

import com.seguridata.tools.dbmigrator.data.entity.DefinitionEntity;
import com.seguridata.tools.dbmigrator.data.entity.TableEntity;

import java.util.Collection;

public interface DBQueryResolver {
    String verificationQuery();
    String selectFromSourceTableQuery(TableEntity table, Collection<DefinitionEntity> definitions);
    String insertToTargetTableQuery(TableEntity table, Collection<DefinitionEntity> definitions);
}
