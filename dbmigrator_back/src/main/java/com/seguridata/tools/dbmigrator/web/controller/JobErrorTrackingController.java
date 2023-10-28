package com.seguridata.tools.dbmigrator.web.controller;

import com.seguridata.tools.dbmigrator.business.facade.ErrorTrackingFacade;
import com.seguridata.tools.dbmigrator.data.model.ErrorTrackingModel;
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
@RequestMapping("/jobs/{jobId}/errors")
public class JobErrorTrackingController {

    private final ErrorTrackingFacade errorTrackingFacade;

    @Autowired
    public JobErrorTrackingController(ErrorTrackingFacade errorTrackingFacade) {
        this.errorTrackingFacade = errorTrackingFacade;
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<List<ErrorTrackingModel>>> getJobErrors(@PathVariable String jobId) {
        ResponseWrapper<List<ErrorTrackingModel>> jobsResponse = this.errorTrackingFacade.getJobErrors(jobId);
        if ("00".equals(jobsResponse.getCode())) {
            return ResponseEntity.ok(jobsResponse);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(jobsResponse);
        }
    }
}
