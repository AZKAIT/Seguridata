package com.seguridata.tools.dbmigrator.business.event;

import com.seguridata.tools.dbmigrator.data.model.ConnectionModel;
import com.seguridata.tools.dbmigrator.data.model.TableModel;
import org.springframework.context.ApplicationEvent;

import java.util.List;

public class TableCreatedEvent extends ApplicationEvent {

    private final ConnectionModel connection;
    private final List<TableModel> tables;

    public TableCreatedEvent(Object source, ConnectionModel connection, List<TableModel> tables) {
        super(source);
        this.connection = connection;
        this.tables = tables;
    }

    public List<TableModel> getTable() {
        return tables;
    }

    public ConnectionModel getConnection() {
        return connection;
    }
}
