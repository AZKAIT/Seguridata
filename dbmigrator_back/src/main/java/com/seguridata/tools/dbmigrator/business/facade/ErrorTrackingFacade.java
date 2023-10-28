package com.seguridata.tools.dbmigrator.business.facade;

import com.seguridata.tools.dbmigrator.business.exception.BaseCodeException;
import com.seguridata.tools.dbmigrator.business.mapper.ErrorTrackingMapper;
import com.seguridata.tools.dbmigrator.business.service.ErrorTrackingService;
import com.seguridata.tools.dbmigrator.business.service.JobService;
import com.seguridata.tools.dbmigrator.data.entity.ErrorTrackingEntity;
import com.seguridata.tools.dbmigrator.data.entity.JobEntity;
import com.seguridata.tools.dbmigrator.data.model.ErrorTrackingModel;
import com.seguridata.tools.dbmigrator.data.wrapper.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ErrorTrackingFacade {

    private final JobService jobService;
    private final ErrorTrackingService errorTrackingService;
    private final ErrorTrackingMapper errorMapper;

    @Autowired
    public ErrorTrackingFacade(JobService jobService,
                               ErrorTrackingService errorTrackingService,
                               ErrorTrackingMapper errorMapper) {
        this.jobService = jobService;
        this.errorTrackingService = errorTrackingService;
        this.errorMapper = errorMapper;
    }

    public ResponseWrapper<List<ErrorTrackingModel>> getJobErrors(String jobId) {
        ResponseWrapper<List<ErrorTrackingModel>> errorsResponse = new ResponseWrapper<>();
        try {
            JobEntity job = this.jobService.findJobById(jobId);

            List<ErrorTrackingEntity> jobErrors = this.errorTrackingService.getErrorTrackingForJob(job);
            errorsResponse.setCode("00");
            errorsResponse.setData(this.errorMapper.mapErrorModels(jobErrors));
        } catch (BaseCodeException e) {
            errorsResponse.setCode(e.getCode());
            errorsResponse.setMessages(Arrays.asList(e.getMessages()));
        }

        return errorsResponse;
    }
}
