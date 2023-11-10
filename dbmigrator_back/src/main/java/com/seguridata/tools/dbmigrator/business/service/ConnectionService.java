package com.seguridata.tools.dbmigrator.business.service;

import com.seguridata.tools.dbmigrator.business.exception.DuplicateDataException;
import com.seguridata.tools.dbmigrator.business.exception.EmptyResultException;
import com.seguridata.tools.dbmigrator.business.exception.MissingObjectException;
import com.seguridata.tools.dbmigrator.business.exception.ObjectLockedException;
import com.seguridata.tools.dbmigrator.data.entity.ConnectionEntity;
import com.seguridata.tools.dbmigrator.data.repository.ConnectionRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.Boolean.TRUE;

@Service
public class ConnectionService {

    private final ConnectionRepository connectionRepo;

    @Autowired
    public ConnectionService(ConnectionRepository connectionRepo) {
        this.connectionRepo = connectionRepo;
    }


    public ConnectionEntity createConnection(ConnectionEntity connection) {
        connection.setId(null);
        connection.setLocked(false);

        if (this.connectionRepo.validateConnectionData(connection)) {
            throw new DuplicateDataException("Esta Conexión ya existe");
        }

        return this.connectionRepo.insertConnection(connection);
    }

    public ConnectionEntity getConnection(String id) {
        ConnectionEntity connection = this.connectionRepo.getConnection(id);

        if (Objects.isNull(connection)) {
            throw new MissingObjectException("Esta Conexión no existe");
        }

        return connection;
    }

    public List<ConnectionEntity> getAllConnections() {
        List<ConnectionEntity> connectionList = this.connectionRepo.getConnectionList();
        if (CollectionUtils.isEmpty(connectionList)) {
            throw new EmptyResultException("No se encontraron Conexiones");
        }
        return connectionList;
    }

    public ConnectionEntity updateConnection(ConnectionEntity existingConnection, ConnectionEntity connection) {
        connection.setId(existingConnection.getId());
        connection.setLocked(false);

        if (StringUtils.isEmpty(connection.getPassword())) {
            connection.setPassword(existingConnection.getPassword());
        }

        return this.connectionRepo.updateConnection(connection);
    }

    public void validateConnectionStatus(ConnectionEntity connection) {
        if (Objects.isNull(connection)) {
            throw new MissingObjectException("No se encontró la Conexión asociada");
        }

        if (TRUE.equals(connection.getLocked())) {
            throw new ObjectLockedException("La Conexión está bloqueada, no se puede proceder");
        }
    }

    public ConnectionEntity deleteConnection(ConnectionEntity connection) {
        return this.connectionRepo.deleteConnection(connection.getId());
    }

    public boolean lockConnections(ConnectionEntity... connections) {
        return this.changeConnectionLock(connections, true);
    }

    public boolean unlockConnections(ConnectionEntity... connections) {
        return this.changeConnectionLock(connections, false);
    }

    private boolean changeConnectionLock(ConnectionEntity[] connections, boolean locked) {
        List<String> connIds = Arrays.stream(connections).map(ConnectionEntity::getId).collect(Collectors.toList());
        return this.connectionRepo.updateConnectionLock(connIds, locked);
    }
}
