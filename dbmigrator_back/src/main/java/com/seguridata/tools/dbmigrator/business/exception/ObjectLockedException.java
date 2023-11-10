package com.seguridata.tools.dbmigrator.business.exception;

public class ObjectLockedException extends BaseCodeException {
    public ObjectLockedException(String... messages) {
        super("05", messages);
    }
}
