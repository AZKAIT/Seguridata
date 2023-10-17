package com.seguridata.tools.dbmigrator.business.factory;

import com.seguridata.tools.dbmigrator.business.exception.EmptyResultException;
import com.seguridata.tools.dbmigrator.business.exception.MissingObjectException;
import com.seguridata.tools.dbmigrator.business.thread.MigrationThreadPoolExecutor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Component
public class ThreadPoolExecutorFactory {

    private final Map<String, MigrationThreadPoolExecutor> executorServices;

    public ThreadPoolExecutorFactory() {
        this.executorServices = new HashMap<>();
    }

    public MigrationThreadPoolExecutor getExecutorForProject(String projectId, boolean createNew) {
        if (StringUtils.isBlank(projectId)) {
            throw new MissingObjectException("Project Id for Executor Service cannot be empty");
        }

        MigrationThreadPoolExecutor threadPoolExecutor;
        if (!this.executorServices.containsKey(projectId)) {
            if (createNew) {
                threadPoolExecutor = this.createThreadPoolTaskExecutor();
                this.executorServices.put(projectId, threadPoolExecutor);
            } else {
                throw new EmptyResultException("No Executor Service for given Project");
            }
        } else {
            threadPoolExecutor = this.executorServices.get(projectId);
        }

        return threadPoolExecutor;
    }

    public void removeExecutorForProject(String projectId) {
        if (this.executorServices.containsKey(projectId)) {
            MigrationThreadPoolExecutor threadPoolExecutor = this.executorServices.remove(projectId);
            threadPoolExecutor.shutdown();
        }
    }


    private MigrationThreadPoolExecutor createThreadPoolTaskExecutor() {
        return new MigrationThreadPoolExecutor(5, 50, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());
    }
}
