package com.seguridata.tools.dbmigrator.data.entity;

import com.seguridata.tools.dbmigrator.data.constant.ColumnDataType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Document(collection = "columns")
@Getter @Setter
public class ColumnEntity {
    @Id
    private String id;
    private String name;
    private String description;
    private ColumnDataType dataType;
    private Long dataLength;
    private Boolean sorting;
    private Boolean identity;
    @DocumentReference(lazy = true)
    private TableEntity table;
}
