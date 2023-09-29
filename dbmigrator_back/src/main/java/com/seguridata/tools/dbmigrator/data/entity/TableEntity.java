package com.seguridata.tools.dbmigrator.data.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@Document(collection = "tables")
@Getter @Setter
public class TableEntity {
    @Id
    private String id;
    private String schema;
    private String name;
    private String description;
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
