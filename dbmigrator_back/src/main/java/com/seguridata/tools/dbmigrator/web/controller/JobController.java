package com.seguridata.tools.dbmigrator.web.controller;

import com.seguridata.tools.dbmigrator.business.facade.JobFacade;
import com.seguridata.tools.dbmigrator.data.model.JobModel;
import com.seguridata.tools.dbmigrator.data.wrapper.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/jobs")
public class JobController {

    private final JobFacade jobFacade;

    @Autowired
    public JobController(JobFacade jobFacade) {
        this.jobFacade = jobFacade;
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<List<JobModel>>> getAllJobs() {
        ResponseWrapper<List<JobModel>> jobsResponse = this.jobFacade.getAllJobs();
        if ("00".equals(jobsResponse.getCode())) {
            return ResponseEntity.ok(jobsResponse);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(jobsResponse);
        }
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<ResponseWrapper<JobModel>> getJobById(@PathVariable String jobId) {
        ResponseWrapper<JobModel> jobResponse = this.jobFacade.getJobById(jobId);
        if ("00".equals(jobResponse.getCode())) {
            return ResponseEntity.ok(jobResponse);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(jobResponse);
        }
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<ResponseWrapper<List<JobModel>>> getProjectJobs(@PathVariable String projectId) {
        ResponseWrapper<List<JobModel>> jobsResponse = this.jobFacade.getProjectJobs(projectId);
        if ("00".equals(jobsResponse.getCode())) {
            return ResponseEntity.ok(jobsResponse);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(jobsResponse);
        }
    }
}
