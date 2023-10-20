package com.seguridata.tools.dbmigrator.data.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.seguridata.tools.dbmigrator.data.constant.ProjectStatus;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter @Setter
public class ProjectModel {
    private String id;
    @NotBlank
    private String name;
    private String description;
    @NotBlank
    private ConnectionModel sourceConnection;
    @NotBlank
    private ConnectionModel targetConnection;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date createdAt;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ProjectStatus status;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date lastStatusDate;
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private Boolean autoPopulate;
}
