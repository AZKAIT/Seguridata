package com.seguridata.tools.dbmigrator.web.controller;

import com.seguridata.tools.dbmigrator.business.facade.TableFacade;
import com.seguridata.tools.dbmigrator.data.model.TableModel;
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

import javax.validation.Valid;

@RestController
@RequestMapping("tables/{tableId}")
public class TableController {

    private final TableFacade tableFacade;

    @Autowired
    public TableController(TableFacade tableFacade) {
        this.tableFacade = tableFacade;
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<TableModel>> getTable(@PathVariable String tableId) {
        ResponseWrapper<TableModel> tableResponse = this.tableFacade.getTable(tableId);

        if ("00".equals(tableResponse.getCode())) {
            return ResponseEntity.ok(tableResponse);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(tableResponse);
        }
    }

    @PutMapping
    public ResponseEntity<ResponseWrapper<TableModel>> updateTable(@PathVariable String tableId,
                                                                   @RequestBody @Valid TableModel tableModel) {
        ResponseWrapper<TableModel> tableResponse = this.tableFacade.updateTable(tableId, tableModel);

        if ("00".equals(tableResponse.getCode())) {
            return ResponseEntity.ok(tableResponse);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(tableResponse);
        }
    }
}
