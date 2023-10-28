package com.seguridata.tools.dbmigrator.business.facade;

import com.seguridata.tools.dbmigrator.business.exception.BaseCodeException;
import com.seguridata.tools.dbmigrator.business.mapper.JobMapper;
import com.seguridata.tools.dbmigrator.business.service.JobService;
import com.seguridata.tools.dbmigrator.business.service.ProjectService;
import com.seguridata.tools.dbmigrator.data.entity.JobEntity;
import com.seguridata.tools.dbmigrator.data.entity.ProjectEntity;
import com.seguridata.tools.dbmigrator.data.model.JobModel;
import com.seguridata.tools.dbmigrator.data.wrapper.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class JobFacade {

    private final ProjectService projectService;
    private final JobService jobService;

    private final JobMapper jobMapper;

    @Autowired
    public JobFacade(ProjectService projectService,
                     JobService jobService,
                     JobMapper jobMapper) {
        this.projectService = projectService;
        this.jobService = jobService;
        this.jobMapper = jobMapper;
    }

    public ResponseWrapper<List<JobModel>> getAllJobs() {
        ResponseWrapper<List<JobModel>> jobsResponse = new ResponseWrapper<>();
        try {
            List<JobEntity> jobs = this.jobService.findAllJobs();

            jobsResponse.setCode("00");
            jobsResponse.setData(this.jobMapper.mapJobModels(jobs));
        } catch (BaseCodeException e) {
            jobsResponse.setCode(e.getCode());
            jobsResponse.setMessages(Arrays.asList(e.getMessages()));
        }

        return jobsResponse;
    }

    public ResponseWrapper<JobModel> getJobById(String jobId) {
        ResponseWrapper<JobModel> jobsResponse = new ResponseWrapper<>();
        try {
            JobEntity job = this.jobService.findJobById(jobId);

            jobsResponse.setCode("00");
            jobsResponse.setData(this.jobMapper.mapJobModel(job));
        } catch (BaseCodeException e) {
            jobsResponse.setCode(e.getCode());
            jobsResponse.setMessages(Arrays.asList(e.getMessages()));
        }

        return jobsResponse;
    }

    public ResponseWrapper<List<JobModel>> getProjectJobs(String projectId) {
        ResponseWrapper<List<JobModel>> jobsResponse = new ResponseWrapper<>();
        try {
            ProjectEntity project = this.projectService.getProject(projectId);

            List<JobEntity> jobs = this.jobService.findJobsForProject(project);

            jobsResponse.setCode("00");
            jobsResponse.setData(this.jobMapper.mapJobModels(jobs));
        } catch (BaseCodeException e) {
            jobsResponse.setCode(e.getCode());
            jobsResponse.setMessages(Arrays.asList(e.getMessages()));
        }

        return jobsResponse;
    }
}
