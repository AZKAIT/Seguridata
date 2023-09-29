package com.seguridata.tools.dbmigrator.data.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.seguridata.tools.dbmigrator.data.constant.ColumnDataType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter @Setter
public class ColumnModel {
    private String id;
    @NotBlank
    private String name;
    private String description;
    @NotNull
    private ColumnDataType dataType;
    @NotNull
    private Long dataLength;

    public ColumnModel() {
    }

    public ColumnModel(String id) {
        this.id = id;
    }
}
