package com.seguridata.tools.dbmigrator.data.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter @Setter
public class PlanModel implements Serializable {
	private static final long serialVersionUID = 4492723680598809829L;

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
