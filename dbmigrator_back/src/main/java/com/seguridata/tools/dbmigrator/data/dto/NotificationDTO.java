package com.seguridata.tools.dbmigrator.data.dto;

import com.seguridata.tools.dbmigrator.data.constant.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class NotificationDTO {
    private String referenceId;
    private String objectType;
    private String objectName;
    private Object data;
}
