package com.seguridata.tools.dbmigrator.data.entity;

import com.seguridata.tools.dbmigrator.data.constant.JobStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Document(collection = "jobs")
@Getter @Setter
public class JobEntity implements Serializable {
	private static final long serialVersionUID = 8853766360509377235L;

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
    private List<ExecutionStatisticsEntity> planStats;


    @Transient
    public String getProjectExecutionNumber() {
        return String.format("%s#%s", this.projectName, this.execNumber);
    }
}
