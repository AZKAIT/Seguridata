package com.seguridata.tools.dbmigrator.data.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter @Setter
public class ConnectionTestResult {
    private boolean successful;
    private String message;

    public ConnectionTestResult() {
        this.successful = false;
        this.message = StringUtils.EMPTY;
    }
}
