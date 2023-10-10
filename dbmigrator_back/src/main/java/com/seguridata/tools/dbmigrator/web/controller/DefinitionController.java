package com.seguridata.tools.dbmigrator.web.controller;

import com.seguridata.tools.dbmigrator.business.facade.DefinitionFacade;
import com.seguridata.tools.dbmigrator.data.model.DefinitionModel;
import com.seguridata.tools.dbmigrator.data.wrapper.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("definitions/{definitionId}")
public class DefinitionController {
    private final DefinitionFacade definitionFacade;

    @Autowired
    public DefinitionController(DefinitionFacade definitionFacade) {
        this.definitionFacade = definitionFacade;
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<DefinitionModel>> getDefinition(@PathVariable String definitionId) {
        ResponseWrapper<DefinitionModel> definitionResponse = this.definitionFacade.getDefinition(definitionId);
        if ("00".equals(definitionResponse.getCode())) {
            return ResponseEntity.ok(definitionResponse);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(definitionResponse);
        }
    }

    @PutMapping
    public ResponseEntity<ResponseWrapper<DefinitionModel>> updateDefinition(@PathVariable String definitionId,
                                                                             @Valid @RequestBody DefinitionModel definitionModel) {
        ResponseWrapper<DefinitionModel> definitionResponse = this.definitionFacade.updateDefinition(definitionId, definitionModel);
        if ("00".equals(definitionResponse.getCode())) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(definitionResponse);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(definitionResponse);
        }
    }
}
