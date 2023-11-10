package com.seguridata.tools.dbmigrator.data.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter @Setter
public class ErrorTrackingModel implements Serializable {
	private static final long serialVersionUID = 6416377010960955707L;

	private String id;
    private Date date;
    private String message;
    private String referenceId;
    private String referenceType;
}
