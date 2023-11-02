package com.seguridata.tools.dbmigrator.data.model;

import com.seguridata.tools.dbmigrator.data.constant.ExecutionResult;
import com.seguridata.tools.dbmigrator.data.constant.ExecutionStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ExecutionStatisticsModel {
    private String name;
    private ExecutionResult result;
    private ExecutionStatus status;
    private Double progress;
}
