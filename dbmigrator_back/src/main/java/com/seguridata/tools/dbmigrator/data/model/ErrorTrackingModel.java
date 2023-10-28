package com.seguridata.tools.dbmigrator.data.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
public class ErrorTrackingModel {
    private String id;
    private Date date;
    private String message;
    private String referenceId;
    private String referenceType;
}
