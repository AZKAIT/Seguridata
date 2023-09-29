package com.seguridata.tools.dbmigrator.business.exception;

public class DBValidationException extends BaseCodeException {
    public DBValidationException(String... messages) {
        super("02");
        this.messages = messages;
    }
}
