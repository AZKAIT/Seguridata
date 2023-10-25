package com.seguridata.tools.dbmigrator.data.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@Document(collection = "tables")
@CompoundIndex(name = "connection_schema_name", def = "{'connection': 1, 'schema': 1, 'name': 1}", unique = true)
@Getter @Setter
public class TableEntity {
    @Id
    private String id;
    private String schema;
    private String name;
    private String description;
    private String orderColumnName;
    @DocumentReference(lazy = true)
    private ConnectionEntity connection;
    @ReadOnlyProperty
    @DocumentReference(lookup="{'table':?#{#self._id} }")
    private List<ColumnEntity> columns;

    public TableEntity() {
    }

    public TableEntity(String tableId) {
        this.id = tableId;
    }
}
