package com.seguridata.tools.dbmigrator.web.controller;

import com.seguridata.tools.dbmigrator.business.facade.DashboardFacade;
import com.seguridata.tools.dbmigrator.data.model.DashboardDataModel;
import com.seguridata.tools.dbmigrator.data.wrapper.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardFacade dashboardFacade;

    @Autowired
    public DashboardController(DashboardFacade dashboardFacade) {
        this.dashboardFacade = dashboardFacade;
    }


    @GetMapping
    public ResponseEntity<ResponseWrapper<DashboardDataModel>> getDashboardData() {
        ResponseWrapper<DashboardDataModel> dashboardResponse = this.dashboardFacade.getDashboardData();

        if ("00".equals(dashboardResponse.getCode())) {
            return ResponseEntity.ok(dashboardResponse);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(dashboardResponse);
        }
    }
}
