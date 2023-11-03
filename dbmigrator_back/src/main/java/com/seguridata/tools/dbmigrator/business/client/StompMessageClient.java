package com.seguridata.tools.dbmigrator.business.client;

import com.seguridata.tools.dbmigrator.data.constant.ExecutionResult;
import com.seguridata.tools.dbmigrator.data.constant.ExecutionStatus;
import com.seguridata.tools.dbmigrator.data.constant.NotificationType;
import com.seguridata.tools.dbmigrator.data.constant.JobStatus;
import com.seguridata.tools.dbmigrator.data.dto.NotificationDTO;
import com.seguridata.tools.dbmigrator.data.entity.ConnectionEntity;
import com.seguridata.tools.dbmigrator.data.entity.ErrorTrackingEntity;
import com.seguridata.tools.dbmigrator.data.entity.ExecutionStatisticsEntity;
import com.seguridata.tools.dbmigrator.data.entity.JobEntity;
import com.seguridata.tools.dbmigrator.data.entity.ProjectEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

import static com.seguridata.tools.dbmigrator.business.constant.TopicConstants.JOB_EXECUTION_ERROR_TOPIC;
import static com.seguridata.tools.dbmigrator.business.constant.TopicConstants.JOB_EXECUTION_STATS_TOPIC;
import static com.seguridata.tools.dbmigrator.business.constant.TopicConstants.JOB_EXECUTION_STATUS_TOPIC;
import static com.seguridata.tools.dbmigrator.business.constant.TopicConstants.CONNECTION_SYNC_UP_ERROR_TOPIC;
import static com.seguridata.tools.dbmigrator.business.constant.TopicConstants.CONNECTION_SYNC_UP_STATUS_TOPIC;
import static com.seguridata.tools.dbmigrator.business.constant.TopicConstants.PROJECT_SYNC_UP_ERROR_TOPIC;
import static com.seguridata.tools.dbmigrator.business.constant.TopicConstants.PROJECT_SYNC_UP_STATUS_TOPIC;
import static com.seguridata.tools.dbmigrator.data.constant.NotificationType.JOB_EXECUTION_ERROR;
import static com.seguridata.tools.dbmigrator.data.constant.NotificationType.JOB_EXECUTION_STATS;
import static com.seguridata.tools.dbmigrator.data.constant.NotificationType.JOB_EXECUTION_STATUS;
import static com.seguridata.tools.dbmigrator.data.constant.NotificationType.CONNECTION_SYNC_UP_ERROR;
import static com.seguridata.tools.dbmigrator.data.constant.NotificationType.CONNECTION_SYNC_UP_STATUS;
import static com.seguridata.tools.dbmigrator.data.constant.NotificationType.PROJECT_SYNC_UP_ERROR;
import static com.seguridata.tools.dbmigrator.data.constant.NotificationType.PROJECT_SYNC_UP_STATUS;

@Component
public class StompMessageClient {

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final Map<NotificationType, String> notificationTypeTopics;

    @Autowired
    public StompMessageClient(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;

        this.notificationTypeTopics = new EnumMap<>(NotificationType.class);
        this.notificationTypeTopics.put(JOB_EXECUTION_STATUS, JOB_EXECUTION_STATUS_TOPIC);
        this.notificationTypeTopics.put(JOB_EXECUTION_ERROR, JOB_EXECUTION_ERROR_TOPIC);
        this.notificationTypeTopics.put(CONNECTION_SYNC_UP_STATUS, CONNECTION_SYNC_UP_STATUS_TOPIC);
        this.notificationTypeTopics.put(CONNECTION_SYNC_UP_ERROR, CONNECTION_SYNC_UP_ERROR_TOPIC);
        this.notificationTypeTopics.put(PROJECT_SYNC_UP_STATUS, PROJECT_SYNC_UP_STATUS_TOPIC);
        this.notificationTypeTopics.put(PROJECT_SYNC_UP_ERROR, PROJECT_SYNC_UP_ERROR_TOPIC);
        this.notificationTypeTopics.put(JOB_EXECUTION_STATS, JOB_EXECUTION_STATS_TOPIC);
    }

    public void sendJobStatusChange(JobEntity job, JobStatus newStatus) {
        NotificationDTO notification = new NotificationDTO(job.getId(), JobEntity.class.getCanonicalName(),
                job.getProjectExecutionNumber(), newStatus);

        this.sendNotification(JOB_EXECUTION_STATUS, notification);
    }

    public void sendJobExecutionError(JobEntity job, ErrorTrackingEntity errorTrack) {
        NotificationDTO notification = new NotificationDTO(job.getId(), JobEntity.class.getCanonicalName(),
                job.getProjectExecutionNumber(), errorTrack.getMessage());

        this.sendNotification(JOB_EXECUTION_ERROR, notification);
    }

    public void sendConnSyncUpStatusChange(ConnectionEntity connection, String message) {
        String connId = Objects.isNull(connection) ? "" : connection.getId();
        String connName = Objects.isNull(connection) ? "N/A" : connection.getName();

        NotificationDTO notification = new NotificationDTO(connId, ConnectionEntity.class.getCanonicalName(),
                connName, message);

        this.sendNotification(CONNECTION_SYNC_UP_STATUS, notification);
    }

    public void sendConnSyncUpError(ConnectionEntity connection, String message) {
        String connId = Objects.isNull(connection) ? "" : connection.getId();
        String connName = Objects.isNull(connection) ? "N/A" : connection.getName();

        NotificationDTO notification = new NotificationDTO(connId, ConnectionEntity.class.getCanonicalName(),
                connName, message);

        this.sendNotification(CONNECTION_SYNC_UP_ERROR, notification);
    }

    public void sendProjectSyncUpStatusChange(ProjectEntity project, String message) {
        String projId = Objects.isNull(project) ? "" : project.getId();
        String projName = Objects.isNull(project) ? "N/A" : project.getName();

        NotificationDTO notification = new NotificationDTO(projId, ProjectEntity.class.getCanonicalName(),
                projName, message);

        this.sendNotification(PROJECT_SYNC_UP_STATUS, notification);
    }

    public void sendProjectSyncUpError(ProjectEntity project, String message) {
        String projId = Objects.isNull(project) ? "" : project.getId();
        String projName = Objects.isNull(project) ? "N/A" : project.getName();

        NotificationDTO notification = new NotificationDTO(projId, ProjectEntity.class.getCanonicalName(),
                projName, message);

        this.sendNotification(PROJECT_SYNC_UP_ERROR, notification);
    }

    public void sendExecutionStats(String jobId, String planId, ExecutionStatus execStatus, Double progress, ExecutionResult execResult) {
        ExecutionStatisticsEntity execStats = new ExecutionStatisticsEntity();
        execStats.setPlanId(planId);
        execStats.setStatus(execStatus);
        execStats.setProgress(progress);
        execStats.setResult(execResult);

        NotificationDTO notification = new NotificationDTO(jobId, JobEntity.class.getCanonicalName(), "", execStats);

        this.sendNotification(JOB_EXECUTION_STATS, notification);
    }


    private void sendNotification(NotificationType type, NotificationDTO notification) {
        String topic = this.notificationTypeTopics.get(type);
        this.simpMessagingTemplate.convertAndSend(topic, notification);
    }
}
