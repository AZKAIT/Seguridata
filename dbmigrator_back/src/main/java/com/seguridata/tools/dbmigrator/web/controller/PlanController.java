package com.seguridata.tools.dbmigrator.web.controller;

import com.seguridata.tools.dbmigrator.business.facade.PlanFacade;
import com.seguridata.tools.dbmigrator.data.model.PlanModel;
import com.seguridata.tools.dbmigrator.data.wrapper.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("plans/{planId}")
public class PlanController {

    private final PlanFacade planFacade;

    @Autowired
    public PlanController(PlanFacade planFacade) {
        this.planFacade = planFacade;
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<PlanModel>> getPlan(@PathVariable String planId) {
        ResponseWrapper<PlanModel> planResponse = this.planFacade.getPlan(planId);
        if ("00".equals(planResponse.getCode())) {
            return ResponseEntity.ok(planResponse);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(planResponse);
        }
    }

    @PutMapping
    public ResponseEntity<ResponseWrapper<PlanModel>> updatePlan(@PathVariable String planId,
                                                                 @Valid @RequestBody PlanModel planModel) {
        ResponseWrapper<PlanModel> planResponse = this.planFacade.updatePlan(planId, planModel);
        if ("00".equals(planResponse.getCode())) {
            return ResponseEntity.ok(planResponse);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(planResponse);
        }
    }

    @DeleteMapping
    public ResponseEntity<ResponseWrapper<PlanModel>> deletePlan(@PathVariable String planId) {
        ResponseWrapper<PlanModel> planResponse = this.planFacade.deletePlan(planId);
        if ("00".equals(planResponse.getCode())) {
            return ResponseEntity.ok(planResponse);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(planResponse);
        }
    }
}
