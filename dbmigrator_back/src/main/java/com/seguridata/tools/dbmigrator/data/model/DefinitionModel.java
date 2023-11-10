package com.seguridata.tools.dbmigrator.data.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.seguridata.tools.dbmigrator.data.constant.ConversionFunctionType;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter @Setter
public class DefinitionModel implements Serializable {
	private static final long serialVersionUID = -2180973540557254717L;

	private String id;
    @NotBlank
    private ColumnModel sourceColumn;
    @NotBlank
    private ColumnModel targetColumn;
    @NotNull
    private ConversionFunctionType conversionFunction;
}
