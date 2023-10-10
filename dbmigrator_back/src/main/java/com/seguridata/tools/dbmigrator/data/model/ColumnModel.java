package com.seguridata.tools.dbmigrator.data.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.seguridata.tools.dbmigrator.data.constant.ColumnDataType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


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
