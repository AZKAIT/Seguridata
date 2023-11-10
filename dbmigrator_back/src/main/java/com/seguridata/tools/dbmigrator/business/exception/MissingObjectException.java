package com.seguridata.tools.dbmigrator.business.exception;

public class MissingObjectException extends BaseCodeException {
    public MissingObjectException(String... messages) {
        super("03", messages);
    }
}
