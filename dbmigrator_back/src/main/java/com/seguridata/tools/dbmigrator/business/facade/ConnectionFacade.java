package com.seguridata.tools.dbmigrator.business.facade;

import com.seguridata.tools.dbmigrator.business.event.ConnectionCreatedEvent;
import com.seguridata.tools.dbmigrator.business.exception.BaseCodeException;
import com.seguridata.tools.dbmigrator.business.exception.DBValidationException;
import com.seguridata.tools.dbmigrator.business.exception.ObjectLockedException;
import com.seguridata.tools.dbmigrator.business.factory.DatabaseConnectionFactory;
import com.seguridata.tools.dbmigrator.business.mapper.ConnectionMapper;
import com.seguridata.tools.dbmigrator.business.service.ConnectionService;
import com.seguridata.tools.dbmigrator.data.dto.ConnectionTestResult;
import com.seguridata.tools.dbmigrator.data.entity.ConnectionEntity;
import com.seguridata.tools.dbmigrator.data.model.ConnectionModel;
import com.seguridata.tools.dbmigrator.data.wrapper.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

import static java.lang.Boolean.TRUE;

@Component
public class ConnectionFacade {

    private final ConnectionMapper connMapper;
    private final DatabaseConnectionFactory dbConnFactory;
    private final ConnectionService connectionService;
    private final ApplicationEventPublisher appEventPublisher;

    @Autowired
    public ConnectionFacade(ConnectionMapper connMapper,
                            DatabaseConnectionFactory dbConnFactory,
                            ConnectionService connectionService,
                            ApplicationEventPublisher appEventPublisher) {
        this.connMapper = connMapper;
        this.dbConnFactory = dbConnFactory;
        this.connectionService = connectionService;
        this.appEventPublisher = appEventPublisher;
    }

    public ResponseWrapper<ConnectionModel> createNewConnection(@Valid ConnectionModel connectionModel) {
        ResponseWrapper<ConnectionModel> connectionResponse = new ResponseWrapper<>();
        try {
            ConnectionEntity connection = this.connMapper.mapConnectionEntity(connectionModel);

            // Testing if the connection works
            ConnectionTestResult connectionTestResult = this.dbConnFactory.createAndTestConnection(connection);
            if (!connectionTestResult.isSuccessful()) {
                throw new DBValidationException("Connection to DB failed");
            }

            // Saving connection to Database
            connection = this.connectionService.createConnection(connection);

            ConnectionModel resultModel = this.connMapper.mapConnectionModel(connection);
            connectionResponse.setCode("00");
            connectionResponse.setData(resultModel);

            this.appEventPublisher.publishEvent(new ConnectionCreatedEvent(this, resultModel));
        } catch (BaseCodeException e) {
            connectionResponse.setCode(e.getCode());
            connectionResponse.setMessages(Arrays.asList(e.getMessages()));
        }

        return connectionResponse;
    }

    public ResponseWrapper<List<ConnectionModel>> getAllConnections() {
        ResponseWrapper<List<ConnectionModel>> connectionsResponse = new ResponseWrapper<>();

        try {
            List<ConnectionEntity> allConnections = this.connectionService.getAllConnections();
            connectionsResponse.setCode("00");
            connectionsResponse.setData(this.connMapper.mapConnectionModelList(allConnections));
        } catch (BaseCodeException e) {
            connectionsResponse.setCode(e.getCode());
            connectionsResponse.setMessages(Arrays.asList(e.getMessages()));
        }

        return connectionsResponse;
    }

    public ResponseWrapper<ConnectionModel> getConnection(String id) {
        ResponseWrapper<ConnectionModel> connectionResponse = new ResponseWrapper<>();
        try {
            ConnectionEntity connection = this.connectionService.getConnection(id);

            connectionResponse.setCode("00");
            connectionResponse.setData(this.connMapper.mapConnectionModel(connection));
        } catch (BaseCodeException e) {
            connectionResponse.setCode(e.getCode());
            connectionResponse.setMessages(Arrays.asList(e.getMessages()));
        }

        return connectionResponse;
    }

    public ResponseWrapper<ConnectionModel> updateConnection(String connectionId, ConnectionModel connectionModel) {
        ResponseWrapper<ConnectionModel> connectionResponse = new ResponseWrapper<>();
        try {
            ConnectionEntity currentConnection = this.connectionService.getConnection(connectionId);
            if (TRUE.equals(currentConnection.getLocked())) {
                throw new ObjectLockedException("Connection is locked, cannot update");
            }

            currentConnection = this.connectionService.updateConnection(currentConnection, this.connMapper.mapConnectionEntity(connectionModel));
            connectionResponse.setCode("00");
            connectionResponse.setData(this.connMapper.mapConnectionModel(currentConnection));
        } catch (BaseCodeException e) {
            connectionResponse.setCode(e.getCode());
            connectionResponse.setMessages(Arrays.asList(e.getMessages()));
        }

        return connectionResponse;
    }
}
