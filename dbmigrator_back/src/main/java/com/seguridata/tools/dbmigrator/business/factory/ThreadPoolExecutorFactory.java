package com.seguridata.tools.dbmigrator.business.factory;

import com.seguridata.tools.dbmigrator.business.exception.EmptyResultException;
import com.seguridata.tools.dbmigrator.business.exception.InvalidUpdateException;
import com.seguridata.tools.dbmigrator.business.exception.MissingObjectException;
import com.seguridata.tools.dbmigrator.business.thread.MigrationThreadPoolExecutor;
import com.seguridata.tools.dbmigrator.data.entity.JobEntity;
import com.seguridata.tools.dbmigrator.data.entity.ProjectEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Component
public class ThreadPoolExecutorFactory {

    private final Map<String, MigrationThreadPoolExecutor> executorServices;

    public ThreadPoolExecutorFactory() {
        this.executorServices = new HashMap<>();
    }

    public MigrationThreadPoolExecutor initializeExecutorForJob(JobEntity job) {
        if (Objects.isNull(job) || StringUtils.isBlank(job.getId())) {
            throw new MissingObjectException("La Tarea para el servicio Ejecutor no puede estar vacío");
        }

        if (this.executorServices.containsKey(job.getId())) {
            throw new InvalidUpdateException("El servicio Ejecutor de la Tarea ya existe");
        }

        MigrationThreadPoolExecutor threadPoolExecutor = this.createThreadPoolTaskExecutor(job.getProject());
        this.executorServices.put(job.getId(), threadPoolExecutor);

        return threadPoolExecutor;
    }

    public MigrationThreadPoolExecutor getExecutorForProject(String jobId) {
        if (StringUtils.isBlank(jobId)) {
            throw new MissingObjectException("El ID de la Tarea para el servicio Ejecutor no puede estar vacío");
        }

        if (!this.executorServices.containsKey(jobId)) {
            throw new EmptyResultException("No se encontró el servicio ejecutor para esta Tarea");
        }

        return this.executorServices.get(jobId);
    }

    public void removeExecutorForProject(String projectId) {
        if (this.executorServices.containsKey(projectId)) {
            MigrationThreadPoolExecutor threadPoolExecutor = this.executorServices.remove(projectId);
            threadPoolExecutor.shutdown();
        }
    }


    private MigrationThreadPoolExecutor createThreadPoolTaskExecutor(ProjectEntity project) {
        Integer corePoolSize = 1;
        if (Objects.nonNull(project.getParallelThreads())) {
            corePoolSize = project.getParallelThreads();
        }

        return new MigrationThreadPoolExecutor(corePoolSize, 70, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());
    }
}
