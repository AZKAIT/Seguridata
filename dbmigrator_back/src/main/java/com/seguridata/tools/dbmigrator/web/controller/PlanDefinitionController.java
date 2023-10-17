package com.seguridata.tools.dbmigrator.web.controller;

import com.seguridata.tools.dbmigrator.business.facade.DefinitionFacade;
import com.seguridata.tools.dbmigrator.data.model.DefinitionModel;
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
@RequestMapping("plans/{planId}/definitions")
public class PlanDefinitionController {
    private final DefinitionFacade definitionFacade;

    @Autowired
    public PlanDefinitionController(DefinitionFacade definitionFacade) {
        this.definitionFacade = definitionFacade;
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<List<DefinitionModel>>> getPlanDefinitions(@PathVariable String planId) {
        ResponseWrapper<List<DefinitionModel>> definitionsResponse = this.definitionFacade.getPlanDefinitions(planId);
        if ("00".equals(definitionsResponse.getCode())) {
            return ResponseEntity.ok(definitionsResponse);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(definitionsResponse);
        }
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper<DefinitionModel>> createPlanDefinition(@PathVariable String planId,
                                                                                 @Valid @RequestBody DefinitionModel definitionModel) {
        ResponseWrapper<DefinitionModel> definitionResponse = this.definitionFacade.createPlanDefinition(planId, definitionModel);
        if ("00".equals(definitionResponse.getCode())) {
            return ResponseEntity.status(HttpStatus.CREATED).body(definitionResponse);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(definitionResponse);
        }
    }

    @PostMapping("batch")
    public ResponseEntity<ResponseWrapper<List<DefinitionModel>>> createPlanDefinitionList(@PathVariable String planId,
                                                                                           @Valid @RequestBody List<DefinitionModel> definitionModelList) {
        ResponseWrapper<List<DefinitionModel>> definitionsResponse = this.definitionFacade.createPlanDefinitionList(planId, definitionModelList);
        if ("00".equals(definitionsResponse.getCode())) {
            return ResponseEntity.ok(definitionsResponse);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(definitionsResponse);
        }
    }
}
