package com.seguridata.tools.dbmigrator.web.controller;

import com.seguridata.tools.dbmigrator.business.facade.ConnectionFacade;
import com.seguridata.tools.dbmigrator.data.model.ConnectionModel;
import com.seguridata.tools.dbmigrator.data.wrapper.ResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("connections")
public class ConnectionController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionController.class);

    private final ConnectionFacade connectionFacade;

    @Autowired
    public ConnectionController(ConnectionFacade connectionFacade) {
        this.connectionFacade = connectionFacade;
    }

    @GetMapping()
    public ResponseEntity<ResponseWrapper<List<ConnectionModel>>> getAllConnections() {
        LOGGER.debug("Retrieving all connections");
        ResponseWrapper<List<ConnectionModel>> connectionsResponse = this.connectionFacade.getAllConnections();

        if ("00".equals(connectionsResponse.getCode())) {
            return ResponseEntity.ok(connectionsResponse);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(connectionsResponse);
        }
    }

    @GetMapping("{connectionId}")
    public ResponseEntity<ResponseWrapper<ConnectionModel>> getConnection(@PathVariable String connectionId) {
        LOGGER.debug("Retrieving connection by ID: {}", connectionId);
        ResponseWrapper<ConnectionModel> connectionResponse = this.connectionFacade.getConnection(connectionId);

        if ("00".equals(connectionResponse.getCode())) {
            return ResponseEntity.ok(connectionResponse);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(connectionResponse);
        }
    }

    @PostMapping()
    public ResponseEntity<ResponseWrapper<ConnectionModel>> createNewConnection(@RequestBody @Valid ConnectionModel connectionModel) {
        LOGGER.debug("Creating new connection");
        ResponseWrapper<ConnectionModel> connectionResponse = this.connectionFacade.createNewConnection(connectionModel);

        if ("00".equals(connectionResponse.getCode())) {
            return ResponseEntity.status(HttpStatus.CREATED).body(connectionResponse);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(connectionResponse);
        }
    }

    @PutMapping("{connectionId}")
    public ResponseEntity<ResponseWrapper<ConnectionModel>> updateConnection(@PathVariable String connectionId,
                                                                             @RequestBody @Valid ConnectionModel connectionModel) {
        ResponseWrapper<ConnectionModel> connectionResponse = this.connectionFacade
                .updateConnection(connectionId, connectionModel);

        if ("00".equals(connectionResponse.getCode())) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(connectionResponse);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(connectionResponse);
        }
    }

    @DeleteMapping("{connectionId}")
    public ResponseEntity<ResponseWrapper<ConnectionModel>> deleteConnection(@PathVariable String connectionId) {
        LOGGER.debug("Retrieving connection by ID: {}", connectionId);
        ResponseWrapper<ConnectionModel> connectionResponse = this.connectionFacade.deleteConnection(connectionId);

        if ("00".equals(connectionResponse.getCode())) {
            return ResponseEntity.ok(connectionResponse);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(connectionResponse);
        }
    }
}
