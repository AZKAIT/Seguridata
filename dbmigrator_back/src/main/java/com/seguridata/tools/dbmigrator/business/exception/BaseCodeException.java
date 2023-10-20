package com.seguridata.tools.dbmigrator.business.exception;

import java.util.Arrays;
import java.util.stream.Collectors;

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

    @Override
    public String getMessage() {
        return String.join(",", this.messages);
    }
}
