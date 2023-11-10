package com.seguridata.tools.dbmigrator.data.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class NotificationDTO<T extends Serializable> implements Serializable {
	private static final long serialVersionUID = -5496941710349886193L;
	
	private String referenceId;
    private String objectType;
    private String objectName;
    private T data;
}
