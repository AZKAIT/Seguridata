package com.seguridata.tools.dbmigrator.web.controller;

import com.seguridata.tools.dbmigrator.business.facade.TableFacade;
import com.seguridata.tools.dbmigrator.data.model.TableModel;
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
@RequestMapping("connections/{connectionId}/tables")
public class ConnectionTableController {

    private final TableFacade tableFacade;

    @Autowired
    public ConnectionTableController(TableFacade tableFacade) {
        this.tableFacade = tableFacade;
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper<TableModel>> createTable(@PathVariable String connectionId,
                                                                   @RequestBody @Valid TableModel tableModel) {
        ResponseWrapper<TableModel> tableResponse = this.tableFacade.createTable(connectionId, tableModel);
        if ("00".equals(tableResponse.getCode())) {
            return ResponseEntity.ok(tableResponse);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(tableResponse);
        }
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<List<TableModel>>> getTablesForConnection(@PathVariable String connectionId) {
        ResponseWrapper<List<TableModel>> tableResponse = this.tableFacade.getTablesForConnection(connectionId);

        if ("00".equals(tableResponse.getCode())) {
            return ResponseEntity.ok(tableResponse);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(tableResponse);
        }
    }
}
