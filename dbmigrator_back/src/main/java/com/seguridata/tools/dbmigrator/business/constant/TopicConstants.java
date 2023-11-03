package com.seguridata.tools.dbmigrator.business.constant;

public class TopicConstants {
    private TopicConstants() {
    }

    public static final String TOPICS_PREFIX = "/topic";
    public static final String QUEUES_PREFIX = "/queue";
    public static final String APP_SPECIFIC_PREFIX = "/app";
    public static final String JOB_EXECUTION_STATUS_TOPIC = TOPICS_PREFIX + "/job/execution/status";
    public static final String JOB_EXECUTION_ERROR_TOPIC = TOPICS_PREFIX + "/job/execution/error";
    public static final String PROJECT_SYNC_UP_STATUS_TOPIC = TOPICS_PREFIX + "/project/syncup/status";
    public static final String PROJECT_SYNC_UP_ERROR_TOPIC = TOPICS_PREFIX + "/project/syncup/error";
    public static final String CONNECTION_SYNC_UP_STATUS_TOPIC = TOPICS_PREFIX + "/connection/syncup/status";
    public static final String CONNECTION_SYNC_UP_ERROR_TOPIC = TOPICS_PREFIX + "/connection/syncup/error";
    public static final String JOB_EXECUTION_STATS_TOPIC = TOPICS_PREFIX + "/job/execution/stats";
}
