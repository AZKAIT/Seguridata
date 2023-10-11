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
        entity.setObjectService(model.getObjectService());
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

        return ConnectionModel.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .host(entity.getHost())
                .port(entity.getPort())
                .objectService(entity.getObjectService())
                .database(entity.getDatabase())
                .username(entity.getUsername())
                .password("*************").type(entity.getType())
                .locked(entity.getLocked())
                .build();
    }
}
