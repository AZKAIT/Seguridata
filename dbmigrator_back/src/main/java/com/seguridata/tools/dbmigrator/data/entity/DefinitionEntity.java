package com.seguridata.tools.dbmigrator.data.entity;

import com.seguridata.tools.dbmigrator.data.constant.ConversionFunctionType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.io.Serializable;

@Document(collection = "definitions")
@Getter @Setter
public class DefinitionEntity implements Serializable {
	private static final long serialVersionUID = -3750008638304200013L;

	@Id
    private String id;
    @DocumentReference
    private ColumnEntity sourceColumn;
    @DocumentReference
    private ColumnEntity targetColumn;
    private ConversionFunctionType conversionFunction;
    @DocumentReference(lazy = true)
    private PlanEntity plan;
}
