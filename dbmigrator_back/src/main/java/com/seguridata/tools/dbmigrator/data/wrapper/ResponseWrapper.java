package com.seguridata.tools.dbmigrator.data.wrapper;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collection;

@Getter @Setter
public class ResponseWrapper<T> implements Serializable {
	private static final long serialVersionUID = 4415770844663523866L;
	
	private T data; // NOSONAR
    private String code;
    private Collection<String> messages;
}
