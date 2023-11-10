package com.seguridata.tools.dbmigrator.data.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class DashboardDataModel implements Serializable {
	private static final long serialVersionUID = -5073625371681754332L;

	private Long connectionTotal;
    private Long projectTotal;
    private Long jobCount;
}
