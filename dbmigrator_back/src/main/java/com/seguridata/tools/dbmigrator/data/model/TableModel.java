package com.seguridata.tools.dbmigrator.data.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter @Setter
public class TableModel {
    private String id;
    private String schema;
    @NotBlank
    private String name;
    private String description;
    @NotBlank
    private String orderColumnName;
    private String connectionId;
}
