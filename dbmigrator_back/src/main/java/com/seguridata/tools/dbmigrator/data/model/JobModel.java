package com.seguridata.tools.dbmigrator.data.model;

import com.seguridata.tools.dbmigrator.data.constant.JobStatus;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter @Setter
public class JobModel implements Serializable {
	private static final long serialVersionUID = 7918770701148469009L;

	private String id;
    private Long execNumber;
    private String projectName;
    private Date createdAt;
    private Date startedAt;
    private Date finishedAt;
    private JobStatus status;
    private String projectId;
    private List<ExecutionStatisticsModel> planStats;
}
