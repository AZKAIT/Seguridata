package com.seguridata.tools.dbmigrator.data.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DashboardDataDTO implements Serializable {
	private static final long serialVersionUID = 3862136840658298029L;
	
	private Long connectionTotal;
    private Long projectTotal;
    private Long jobCount;
}
