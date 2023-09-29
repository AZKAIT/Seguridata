package com.seguridata.tools.dbmigrator.web.controller;

import com.seguridata.tools.dbmigrator.business.facade.ColumnFacade;
import com.seguridata.tools.dbmigrator.data.model.ColumnModel;
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

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("tables/{tableId}/columns")
public class TableColumnController {

    private final ColumnFacade columnFacade;

    @Autowired
    public TableColumnController(ColumnFacade columnFacade) {
        this.columnFacade = columnFacade;
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<List<ColumnModel>>> getColumnList(@PathVariable String tableId) {
        ResponseWrapper<List<ColumnModel>> columnList = this.columnFacade.getColumnsForTable(tableId);

        if ("00".equals(columnList.getCode())) {
            return ResponseEntity.ok(columnList);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(columnList);
        }
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper<ColumnModel>> createColumn(@PathVariable String tableId,
                                                                     @Valid @RequestBody ColumnModel columnModel) {
        ResponseWrapper<ColumnModel> columnResponse = this.columnFacade.createColumn(tableId, columnModel);

        if ("00".equals(columnResponse.getCode())) {
            return ResponseEntity.status(HttpStatus.CREATED).body(columnResponse);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(columnResponse);
        }
    }

    @PostMapping("batch")
    public ResponseEntity<ResponseWrapper<List<ColumnModel>>> createBatchOfColumns(@PathVariable String tableId,
                                                                                   @Valid @RequestBody List<ColumnModel> columnModelList) {
        ResponseWrapper<List<ColumnModel>> columnsResponse = this.columnFacade.createBatchOfColumns(tableId, columnModelList);

        if ("00".equals(columnsResponse.getCode())) {
            return ResponseEntity.status(HttpStatus.CREATED).body(columnsResponse);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(columnsResponse);
        }
    }
}
