package com.seguridata.tools.dbmigrator.business.mapper;

import com.seguridata.tools.dbmigrator.data.entity.ConnectionEntity;
import com.seguridata.tools.dbmigrator.data.model.ConnectionModel;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ConnectionMapper {

    public List<ConnectionEntity> mapConnectionEntityList(Collection<ConnectionModel> connectionModels) {
        return connectionModels.stream().filter(Objects::nonNull).map(this::mapConnectionEntity).collect(Collectors.toList());
    }

    public List<ConnectionModel> mapConnectionModelList(Collection<ConnectionEntity> connections) {
        return connections.stream().filter(Objects::nonNull).map(this::mapConnectionModel).collect(Collectors.toList());
    }

    public ConnectionEntity mapConnectionEntity(ConnectionModel model) {
        if (Objects.isNull(model)) {
            return null;
        }

        ConnectionEntity entity = new ConnectionEntity();
        entity.setId(model.getId());
        entity.setName(model.getName());
        entity.setDescription(model.getDescription());
        entity.setHost(model.getHost());
        entity.setPort(model.getPort());
        entity.setDatabase(model.getDatabase());
        entity.setUsername(model.getUsername());
        entity.setPassword(model.getPassword());
        entity.setType(model.getType());
        entity.setLocked(model.getLocked());

        return entity;
    }

    public ConnectionModel mapConnectionModel(ConnectionEntity entity) {
        if (Objects.isNull(entity)) {
            return null;
        }

        ConnectionModel model = new ConnectionModel();
        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setDescription(entity.getDescription());
        model.setHost(entity.getHost());
        model.setPort(entity.getPort());
        model.setDatabase(entity.getDatabase());
        model.setUsername(entity.getUsername());
        model.setPassword("*************");
        model.setType(entity.getType());
        model.setLocked(entity.getLocked());

        return model;
    }
}
