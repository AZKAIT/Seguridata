package com.seguridata.tools.dbmigrator.business.exception;

public class DuplicateDataException extends BaseCodeException {
    public DuplicateDataException(String... messages) {
        super("04");
        this.messages = messages;
    }
}
