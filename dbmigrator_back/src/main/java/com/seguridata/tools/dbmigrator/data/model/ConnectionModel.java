package com.seguridata.tools.dbmigrator.data.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.seguridata.tools.dbmigrator.data.constant.DatabaseType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter @Setter
public class ConnectionModel {
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
