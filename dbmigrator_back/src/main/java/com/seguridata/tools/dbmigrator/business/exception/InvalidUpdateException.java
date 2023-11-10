package com.seguridata.tools.dbmigrator.business.exception;

public class InvalidUpdateException extends BaseCodeException {
    public InvalidUpdateException(String... messages) {
        super("06", messages);
    }
}
