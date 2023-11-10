package com.seguridata.tools.dbmigrator.data.model;

import com.seguridata.tools.dbmigrator.data.constant.ExecutionResult;
import com.seguridata.tools.dbmigrator.data.constant.ExecutionStatus;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class ExecutionStatisticsModel implements Serializable {
	private static final long serialVersionUID = 3210797572383776006L;

	private String planId;
    private String planName;
    private ExecutionResult result;
    private ExecutionStatus status;
    private Double progress;
    private Long rowsProcessed;
    private Long rowsForCompletion;
}
