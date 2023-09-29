package com.seguridata.tools.dbmigrator.data.wrapper;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter @Setter
public class ResponseWrapper<T> {
    private T data;
    private String code;
    private Collection<String> messages;
}
