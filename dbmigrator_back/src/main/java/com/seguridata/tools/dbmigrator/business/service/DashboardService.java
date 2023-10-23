package com.seguridata.tools.dbmigrator.business.service;

import com.seguridata.tools.dbmigrator.data.dto.DashboardDataDTO;
import com.seguridata.tools.dbmigrator.data.repository.DashboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final DashboardRepository dashboardRepo;

    @Autowired
    public DashboardService(DashboardRepository dashboardRepo) {
        this.dashboardRepo = dashboardRepo;
    }


    public DashboardDataDTO getDashboardData() {
        return this.dashboardRepo.executeDashboardDataAggregation();
    }
}
