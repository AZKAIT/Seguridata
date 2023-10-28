package com.seguridata.tools.dbmigrator.data.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private Boolean autoPopulate;
    private Boolean locked;
    @Min(1)
    @Max(15)
    @NotNull
    private Integer parallelThreads;
}
