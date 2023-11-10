package com.seguridata.tools.dbmigrator.data.entity;

import com.seguridata.tools.dbmigrator.data.constant.ColumnDataType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.io.Serializable;

@Document(collection = "columns")
@CompoundIndex(name = "table_colname", def = "{'table': 1, 'name': 1}", unique = true)
@Getter @Setter
public class ColumnEntity implements Serializable {
	private static final long serialVersionUID = 2230688581776312209L;
	
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
