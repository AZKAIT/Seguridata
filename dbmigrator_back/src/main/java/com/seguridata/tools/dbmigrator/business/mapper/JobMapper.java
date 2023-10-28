package com.seguridata.tools.dbmigrator.business.mapper;

import com.seguridata.tools.dbmigrator.data.entity.JobEntity;
import com.seguridata.tools.dbmigrator.data.model.JobModel;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JobMapper {

    public List<JobModel> mapJobModels(Collection<JobEntity> job) {
        return job.stream().map(this::mapJobModel).collect(Collectors.toList());
    }

    public JobModel mapJobModel(JobEntity job) {
        JobModel jobModel = new JobModel();
        jobModel.setId(job.getId());
        jobModel.setExecNumber(job.getExecNumber());
        jobModel.setProjectName(job.getProjectName());
        jobModel.setStatus(job.getStatus());
        jobModel.setCreatedAt(job.getCreatedAt());
        jobModel.setStartedAt(job.getStartedAt());
        jobModel.setFinishedAt(job.getFinishedAt());
        jobModel.setProjectId(job.getProject().getId());

        return jobModel;
    }
}
