package com.seguridata.tools.dbmigrator.business.service;

import com.seguridata.tools.dbmigrator.business.client.StompMessageClient;
import com.seguridata.tools.dbmigrator.business.exception.InvalidUpdateException;
import com.seguridata.tools.dbmigrator.business.exception.MissingObjectException;
import com.seguridata.tools.dbmigrator.data.constant.JobStatus;
import com.seguridata.tools.dbmigrator.data.entity.JobEntity;
import com.seguridata.tools.dbmigrator.data.entity.ProjectEntity;
import com.seguridata.tools.dbmigrator.data.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.seguridata.tools.dbmigrator.data.constant.JobStatus.CREATED;
import static com.seguridata.tools.dbmigrator.data.constant.JobStatus.RUNNING;
import static com.seguridata.tools.dbmigrator.data.constant.JobStatus.STARTING;
import static com.seguridata.tools.dbmigrator.data.constant.JobStatus.STOPPED;
import static com.seguridata.tools.dbmigrator.data.constant.JobStatus.STOPPING;

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

        if (STARTING.equals(currentStatus) && (!RUNNING.equals(newStatus) && !STOPPED.equals(newStatus))) {
            throw new InvalidUpdateException("Proyectos en estatus STARTING solo pueden transicionar a estatus RUNNING o STOPPED");
        }

        if (STOPPING.equals(currentStatus) && !STOPPED.equals(newStatus)) {
            throw new InvalidUpdateException("Proyectos en estatus STOPPING solo pueden transicionar a estatus STOPPED");
        }

        job.setStatus(newStatus);
        boolean updated = this.jobRepo.updateJob(job.getId(), newStatus);
        if (updated) {
            this.stompMsgClient.sendJobStatusChange(job, newStatus);
        }
        return updated;
    }
}
