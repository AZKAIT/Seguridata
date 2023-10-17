package com.seguridata.tools.dbmigrator.web.controller;

import com.seguridata.tools.dbmigrator.business.facade.PlanFacade;
import com.seguridata.tools.dbmigrator.data.model.PlanModel;
import com.seguridata.tools.dbmigrator.data.wrapper.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("projects/{projectId}/plans")
public class ProjectPlanController {

    private final PlanFacade planFacade;

    @Autowired
    public ProjectPlanController(PlanFacade planFacade) {
        this.planFacade = planFacade;
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<List<PlanModel>>> getPlansForProject(@PathVariable String projectId) {
        ResponseWrapper<List<PlanModel>> plansResponse = this.planFacade.getPlansForProject(projectId);
        if ("00".equals(plansResponse.getCode())) {
            return ResponseEntity.ok(plansResponse);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(plansResponse);
        }
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper<PlanModel>> createPlanForProject(@PathVariable String projectId,
                                                                           @Valid @RequestBody PlanModel planModel) {
        ResponseWrapper<PlanModel> plansResponse = this.planFacade.createPlanForProject(projectId, planModel);
        if ("00".equals(plansResponse.getCode())) {
            return ResponseEntity.status(HttpStatus.CREATED).body(plansResponse);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(plansResponse);
        }
    }
}
