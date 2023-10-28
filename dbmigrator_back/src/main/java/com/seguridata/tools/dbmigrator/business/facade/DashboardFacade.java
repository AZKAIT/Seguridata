package com.seguridata.tools.dbmigrator.business.facade;

import com.seguridata.tools.dbmigrator.business.service.DashboardService;
import com.seguridata.tools.dbmigrator.data.dto.DashboardDataDTO;
import com.seguridata.tools.dbmigrator.data.model.DashboardDataModel;
import com.seguridata.tools.dbmigrator.data.wrapper.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DashboardFacade {

    private final DashboardService dashboardService;

    @Autowired
    public DashboardFacade(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    public ResponseWrapper<DashboardDataModel> getDashboardData() {
        ResponseWrapper<DashboardDataModel> response = new ResponseWrapper<>();

        DashboardDataDTO dashboardData = this.dashboardService.getDashboardData();
        DashboardDataModel model = this.mapDashboardData(dashboardData);

        response.setCode("00");
        response.setData(model);

        return response;
    }

    private DashboardDataModel mapDashboardData(DashboardDataDTO dashboardData) {
        DashboardDataModel model = new DashboardDataModel();
        model.setConnectionTotal(dashboardData.getConnectionTotal());
        model.setProjectTotal(dashboardData.getProjectTotal());
        model.setJobCount(dashboardData.getJobCount());

        return model;
    }
}
