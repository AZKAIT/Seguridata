package com.seguridata.tools.dbmigrator.business.exception;

public abstract class BaseCodeException extends RuntimeException {
    private final String code;
    protected final String[] messages;

    protected BaseCodeException(String code, String[] messages) {
        this.code = code;
        this.messages = messages;
    }

    public String getCode() {
        return code;
    }

    public String[] getMessages() {
        return messages;
    }

    @Override
    public String getMessage() {
        return String.join(",", this.messages);
    }
}
