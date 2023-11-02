package com.seguridata.tools.dbmigrator.business.thread;

import com.seguridata.tools.dbmigrator.data.constant.ExecutionResult;
import com.seguridata.tools.dbmigrator.business.task.PlanExecutionCallable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MigrationThreadPoolExecutor extends ThreadPoolExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(MigrationThreadPoolExecutor.class);

    private final List<Future<ExecutionResult>> futureList;
    private CountDownLatch latch;

    public MigrationThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                       TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        this.futureList = new ArrayList<>();
    }

    public void invokePlanTasks(Collection<PlanExecutionCallable> planExecutionTasks) {
        this.latch = new CountDownLatch(planExecutionTasks.size());
        planExecutionTasks.forEach(task -> {
            task.initialize(getLatch());
            this.futureList.add(super.submit(task));
        });
    }

    public void stopTasks() {
        LOGGER.info("Stopping Tasks");
        this.futureList.stream()
                .filter(Objects::nonNull)
                .forEach(future -> {
                    if (!future.isDone() && !future.isCancelled()) {
                        future.cancel(true);
                    }
                });
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public List<Future<ExecutionResult>> getFutureList() {
        return futureList;
    }
}
