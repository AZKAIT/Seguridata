package com.seguridata.tools.dbmigrator.web.controller;

import com.seguridata.tools.dbmigrator.business.facade.ColumnFacade;
import com.seguridata.tools.dbmigrator.data.model.ColumnModel;
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
@RequestMapping("columns/{columnId}")
public class ColumnController {

    private final ColumnFacade columnFacade;

    @Autowired
    public ColumnController(ColumnFacade columnFacade) {
        this.columnFacade = columnFacade;
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<ColumnModel>> getColumn(@PathVariable String columnId) {
        ResponseWrapper<ColumnModel> columnResponse = this.columnFacade.getColumn(columnId);
        if ("00".equals(columnResponse.getCode())) {
            return ResponseEntity.ok(columnResponse);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(columnResponse);
        }
    }

    @PutMapping
    public ResponseEntity<ResponseWrapper<ColumnModel>> updateColumn(@PathVariable String columnId,
                                                                     @Valid @RequestBody ColumnModel columnModel) {
        ResponseWrapper<ColumnModel> columnResponse = this.columnFacade.updateColumn(columnId, columnModel);
        if ("00".equals(columnResponse.getCode())) {
            return ResponseEntity.ok(columnResponse);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(columnResponse);
        }
    }
}
