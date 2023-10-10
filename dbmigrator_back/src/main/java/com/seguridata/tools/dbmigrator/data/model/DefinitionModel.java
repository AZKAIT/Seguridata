package com.seguridata.tools.dbmigrator.data.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.seguridata.tools.dbmigrator.data.constant.ConversionFunction;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter @Setter
public class DefinitionModel {
    private String id;
    @NotBlank
    private ColumnModel sourceColumn;
    @NotBlank
    private ColumnModel targetColumn;
    @NotNull
    private ConversionFunction conversionFunction;
}
