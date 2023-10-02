package com.seguridata.tools.dbmigrator.business.event;

import com.seguridata.tools.dbmigrator.data.model.ConnectionModel;
import org.springframework.context.ApplicationEvent;

public class ConnectionCreatedEvent extends ApplicationEvent {

    private final ConnectionModel connection;

    public ConnectionCreatedEvent(Object source, ConnectionModel connection) {
        super(source);
        this.connection = connection;
    }

    public ConnectionModel getConnection() {
        return connection;
    }
}
