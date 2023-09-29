package com.seguridata.tools.dbmigrator.business.exception;

public abstract class BaseCodeException extends RuntimeException {
    private final String code;
    protected String[] messages;

    protected BaseCodeException(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String[] getMessages() {
        return messages;
    }

    public void setMessages(String[] messages) {
        this.messages = messages;
    }
}
