package com.seguridata.tools.dbmigrator.data.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

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
    private Object startId;
    private Object endId;
    private List<Object> idList;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double progressPercent;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String status;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date startDate;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date endDate;

}
