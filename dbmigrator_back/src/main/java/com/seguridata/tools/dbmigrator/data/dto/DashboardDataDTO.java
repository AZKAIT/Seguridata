package com.seguridata.tools.dbmigrator.data.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DashboardDataDTO {
    private Long connectionTotal;
    private Long projectTotal;
    private Long jobCount;
}
