package com.seguridata.tools.dbmigrator.data.entity;

import com.seguridata.tools.dbmigrator.data.constant.ConversionFunction;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Getter @Setter
public class DefinitionEntity {
    @Id
    private String id;
    @DocumentReference
    private ColumnEntity sourceColumn;
    @DocumentReference
    private ColumnEntity targetColumn;
    private ConversionFunction conversionFunction;
    @DocumentReference(lazy = true)
    private PlanEntity plan;
}
