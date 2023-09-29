package com.seguridata.tools.dbmigrator.data.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.Date;

@Document(collection = "errorTracking")
@Getter @Setter
public class ErrorTrackingEntity {
    @Id
    private String id;
    private Date date;
    private String message;
    private String referenceId;
    private String referenceType;

    @DocumentReference(lazy = true)
    private ProjectEntity project;
}
