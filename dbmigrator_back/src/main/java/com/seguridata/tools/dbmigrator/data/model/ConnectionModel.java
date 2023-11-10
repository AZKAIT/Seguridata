package com.seguridata.tools.dbmigrator.data.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.seguridata.tools.dbmigrator.data.constant.DatabaseType;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


import java.io.Serializable;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter @Setter
@Builder(toBuilder = true)
public class ConnectionModel implements Serializable {
	private static final long serialVersionUID = 5704741771615183759L;

	private String id;
    @NotBlank
    private String name;
    private String description;
    @NotBlank
    private String host;
    private Integer port;
    @NotBlank
    private String objectService;
    @NotBlank
    private String database;
    @NotBlank
    private String username;
    @NotBlank
    @JsonProperty(access = WRITE_ONLY)
    private String password;
    private DatabaseType type;
    private Boolean locked;
}
