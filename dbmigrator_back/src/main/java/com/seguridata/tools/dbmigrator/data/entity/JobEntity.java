package com.seguridata.tools.dbmigrator.data.entity;

import com.seguridata.tools.dbmigrator.data.constant.JobStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.Date;
import java.util.Map;

@Document(collection = "jobs")
@Getter @Setter
public class JobEntity {
    @Id
    private String id;
    private Long execNumber;
    private String projectName;
    private Date createdAt;
    private Date startedAt;
    private Date finishedAt;

    private JobStatus status;

    @DocumentReference(lazy = true)
    private ProjectEntity project;
    private Map<String, ExecutionStatisticsEntity> planStats;


    @Transient
    public String getProjectExecutionNumber() {
        return String.format("%s#%s", this.projectName, this.execNumber);
    }
}
