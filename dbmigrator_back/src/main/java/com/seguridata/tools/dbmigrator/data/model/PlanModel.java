package com.seguridata.tools.dbmigrator.data.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter @Setter
public class PlanModel {
    private String id;
    @Min(1)
    private Integer orderNum;
    @NotBlank
    private TableModel sourceTable;
    @NotBlank
    private TableModel targetTable;
    @Min(0)
    private Long initialSkip;
    @Min(1)
    @Max(1000)
    private Long rowLimit;
    private Long maxRows;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double progressPercent;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String status;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date startDate;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date endDate;

}
