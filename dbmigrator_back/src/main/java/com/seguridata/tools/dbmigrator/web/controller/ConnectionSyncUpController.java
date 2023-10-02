package com.seguridata.tools.dbmigrator.web.controller;

import com.seguridata.tools.dbmigrator.business.facade.SyncUpFacade;
import com.seguridata.tools.dbmigrator.data.model.ColumnModel;
import com.seguridata.tools.dbmigrator.data.model.TableModel;
import com.seguridata.tools.dbmigrator.data.wrapper.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("connections/{connectionId}/sync")
public class ConnectionSyncUpController {

    private final SyncUpFacade syncUpFacade;

    @Autowired
    public ConnectionSyncUpController(SyncUpFacade syncUpFacade) {
        this.syncUpFacade = syncUpFacade;
    }

    @PostMapping("tables")
    public ResponseEntity<ResponseWrapper<List<TableModel>>> syncUpConnection(@PathVariable String connectionId) {
        ResponseWrapper<List<TableModel>> tableResponse = this.syncUpFacade.syncUpConnectionTables(connectionId);
        if ("00".equals(tableResponse.getCode())) {
            return ResponseEntity.status(HttpStatus.CREATED).body(tableResponse);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(tableResponse);
        }
    }

    @PostMapping("columns")
    public ResponseEntity<ResponseWrapper<List<ColumnModel>>> syncUpTables(@PathVariable String connectionId) {
        ResponseWrapper<List<ColumnModel>> columnResponse = this.syncUpFacade.syncUpConnectionColumns(connectionId);
        if ("00".equals(columnResponse.getCode())) {
            return ResponseEntity.status(HttpStatus.CREATED).body(columnResponse);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(columnResponse);
        }
    }
}
