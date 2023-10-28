package com.seguridata.tools.dbmigrator.data.model;

import com.seguridata.tools.dbmigrator.data.constant.JobStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
public class JobModel {
    private String id;
    private Long execNumber;
    private String projectName;
    private Date createdAt;
    private Date startedAt;
    private Date finishedAt;
    private JobStatus status;
    private String projectId;
}
