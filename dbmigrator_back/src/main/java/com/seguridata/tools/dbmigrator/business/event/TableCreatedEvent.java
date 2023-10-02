package com.seguridata.tools.dbmigrator.business.event;

import com.seguridata.tools.dbmigrator.data.model.ConnectionModel;
import com.seguridata.tools.dbmigrator.data.model.TableModel;
import org.springframework.context.ApplicationEvent;

public class TableCreatedEvent extends ApplicationEvent {

    private final ConnectionModel connection;
    private final TableModel table;

    public TableCreatedEvent(Object source, ConnectionModel connection, TableModel table) {
        super(source);
        this.connection = connection;
        this.table = table;
    }

    public TableModel getTable() {
        return table;
    }

    public ConnectionModel getConnection() {
        return connection;
    }
}
