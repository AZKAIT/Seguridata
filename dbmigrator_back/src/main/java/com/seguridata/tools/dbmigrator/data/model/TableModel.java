package com.seguridata.tools.dbmigrator.data.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter @Setter
public class TableModel implements Serializable {
	private static final long serialVersionUID = -3825483905218975066L;

	private String id;
    private String schema;
    @NotBlank
    private String name;
    private String description;
    @NotBlank
    private String orderColumnName;
    private String connectionId;
}
