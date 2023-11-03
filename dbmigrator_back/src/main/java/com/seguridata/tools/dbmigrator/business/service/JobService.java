package com.seguridata.tools.dbmigrator.business.service;

import com.seguridata.tools.dbmigrator.business.client.StompMessageClient;
import com.seguridata.tools.dbmigrator.business.exception.InvalidUpdateException;
import com.seguridata.tools.dbmigrator.business.exception.MissingObjectException;
import com.seguridata.tools.dbmigrator.data.constant.ExecutionResult;
import com.seguridata.tools.dbmigrator.data.constant.ExecutionStatus;
import com.seguridata.tools.dbmigrator.data.constant.JobStatus;
import com.seguridata.tools.dbmigrator.data.entity.ExecutionStatisticsEntity;
import com.seguridata.tools.dbmigrator.data.entity.JobEntity;
import com.seguridata.tools.dbmigrator.data.entity.PlanEntity;
import com.seguridata.tools.dbmigrator.data.entity.ProjectEntity;
import com.seguridata.tools.dbmigrator.data.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.seguridata.tools.dbmigrator.data.constant.JobStatus.CREATED;
import static com.seguridata.tools.dbmigrator.data.constant.JobStatus.STARTING;

@Service
public class JobService {

    private final JobRepository jobRepo;
    private final StompMessageClient stompMsgClient;

    @Autowired
    public JobService(JobRepository jobRepo,
                      StompMessageClient stompMsgClient) {
        this.jobRepo = jobRepo;
        this.stompMsgClient = stompMsgClient;
    }

    public JobEntity createJobForProject(ProjectEntity project) {
        JobEntity newJob = new JobEntity();
        Long execNumber = this.jobRepo.countProjectExecutions(project.getId()) + 1;
        newJob.setId(null);
        newJob.setCreatedAt(new Date());
        newJob.setProject(project);
        newJob.setExecNumber(execNumber);
        newJob.setStatus(CREATED);
        newJob.setProjectName(project.getName());
        newJob.setPlanStats(this.createStatisticsForJob(project));

        return this.jobRepo.createJob(newJob);
    }

    public JobEntity findJobById(String jobId) {
        JobEntity job = this.jobRepo.getJob(jobId);

        if (Objects.isNull(job)) {
            throw new MissingObjectException("La Tarea no existe");
        }

        return job;
    }

    public List<JobEntity> findJobsForProject(ProjectEntity project) {
        return this.jobRepo.findJobsForProject(project.getId());
    }

    public List<JobEntity> findAllJobs() {
        return this.jobRepo.getAllJobs();
    }

    public JobEntity findLatestJob(ProjectEntity project) {
        return this.jobRepo.findLatestJobForProject(project.getId());
    }

    public boolean updateProjectStatus(JobEntity job, JobStatus newStatus) {
        if (CREATED.equals(newStatus)) {
            throw new InvalidUpdateException("No se puede regresar el estatus del Proyecto a CREATED");
        }

        JobStatus currentStatus = job.getStatus();
        if (CREATED.equals(currentStatus) && !STARTING.equals(newStatus)) {
            throw new InvalidUpdateException("Proyectos en estatus CREATED solo pueden transicionar a estatus STARTING");
        }

        job.setStatus(newStatus);
        boolean updated = this.jobRepo.updateJob(job.getId(), newStatus);
        if (updated) {
            this.stompMsgClient.sendJobStatusChange(job, newStatus);
        }
        return updated;
    }

    public boolean updateExecutionStatus(String jobId, String planId, ExecutionStatus execStatus) {
        boolean updated = this.jobRepo.updateExecutionStats(jobId, planId, execStatus, null, null);

        this.stompMsgClient.sendExecutionStats(jobId, planId, execStatus, null, null);
        return updated;
    }

    public boolean updateExecutionProgress(String jobId, String planId, long currentRows, long rowsForCompletion) {
        Double progress = ((double) currentRows / (double) rowsForCompletion) * 100;
        boolean updated = this.jobRepo.updateExecutionStats(jobId, planId, null, progress, null);

        this.stompMsgClient.sendExecutionStats(jobId, planId, null, progress, null);
        return updated;
    }

    public boolean updateExecutionResult(String jobId, String planId, ExecutionResult execResult) {
        boolean updated = this.jobRepo.updateExecutionStats(jobId, planId, ExecutionStatus.FINISHED, null, execResult);

        this.stompMsgClient.sendExecutionStats(jobId, planId, ExecutionStatus.FINISHED, null, execResult);
        return updated;
    }

    private List<ExecutionStatisticsEntity> createStatisticsForJob(ProjectEntity project) {
        List<PlanEntity> plans = project.getPlans();
        if (CollectionUtils.isEmpty(plans)) {
            return Collections.emptyList();
        }

        return plans.stream().map(plan -> {
            ExecutionStatisticsEntity stats = new ExecutionStatisticsEntity();
            stats.setPlanId(plan.getId());
            stats.setPlanName(String.format("[%d] %s -> %s", plan.getOrderNum(), plan.getSourceTable().getName(), plan.getTargetTable().getName()));
            stats.setProgress(0.0);
            stats.setStatus(ExecutionStatus.CREATED);

            return stats;
        }).collect(Collectors.toList());
    }
}
