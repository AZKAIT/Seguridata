package com.seguridata.tools.dbmigrator.data.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.seguridata.tools.dbmigrator.data.constant.ColumnDataType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter @Setter
public class ColumnModel implements Serializable {
	private static final long serialVersionUID = -662478351132514935L;

	private String id;
    @NotBlank
    private String name;
    private String description;
    @NotNull
    private ColumnDataType dataType;
    @NotNull
    private Long dataLength;
    private Boolean identity;

    public ColumnModel() {
    }

    public ColumnModel(String id) {
        this.id = id;
    }
}
