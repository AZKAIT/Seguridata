package com.seguridata.tools.dbmigrator.web.listener;

import com.seguridata.tools.dbmigrator.business.event.ConnectionCreatedEvent;
import com.seguridata.tools.dbmigrator.business.event.TableCreatedEvent;
import com.seguridata.tools.dbmigrator.business.facade.SyncUpFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ConnectionCreatedEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionCreatedEventListener.class);

    public final SyncUpFacade syncUpFacade;

    @Autowired
    public ConnectionCreatedEventListener(SyncUpFacade syncUpFacade) {
        this.syncUpFacade = syncUpFacade;
    }

    @Async
    @EventListener
    public void onConnectionCreated(ConnectionCreatedEvent connCreatedEvent) {
        LOGGER.info("Handle onConnectionCreated");
        this.syncUpFacade.syncUpConnectionTables(connCreatedEvent.getConnection());
    }

    @Async
    @EventListener
    public void onTablesCreated(TableCreatedEvent tableCreatedEvent) {
        LOGGER.info("Handle onTableCreated");
        this.syncUpFacade.syncUpSingleTableColumn(tableCreatedEvent.getConnection(), tableCreatedEvent.getTable());
    }
}
