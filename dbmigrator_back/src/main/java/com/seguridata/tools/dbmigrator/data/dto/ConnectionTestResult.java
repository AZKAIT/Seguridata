package com.seguridata.tools.dbmigrator.data.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

@Getter @Setter
public class ConnectionTestResult implements Serializable {
	private static final long serialVersionUID = -4340208221162421647L;
	
	private boolean successful;
    private String message;

    public ConnectionTestResult() {
        this.successful = false;
        this.message = StringUtils.EMPTY;
    }
}
