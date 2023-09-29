package com.seguridata.tools.dbmigrator.business.exception;

public class EmptyResultException extends BaseCodeException {
    public EmptyResultException(String... messages) {
        super("01");
        this.messages = messages;
    }
}
