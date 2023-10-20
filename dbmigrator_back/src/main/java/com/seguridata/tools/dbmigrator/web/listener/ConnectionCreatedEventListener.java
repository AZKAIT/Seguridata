package com.seguridata.tools.dbmigrator.web.listener;

import com.seguridata.tools.dbmigrator.business.event.ConnectionCreatedEvent;
import com.seguridata.tools.dbmigrator.business.event.ProjectCreatedEvent;
import com.seguridata.tools.dbmigrator.business.event.TableCreatedEvent;
import com.seguridata.tools.dbmigrator.business.facade.ConnectionSyncUpFacade;
import com.seguridata.tools.dbmigrator.business.facade.ProjectSyncUpFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ConnectionCreatedEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionCreatedEventListener.class);

    private final ConnectionSyncUpFacade connectionSyncUpFacade;
    private final ProjectSyncUpFacade projectSyncUpFacade;

    @Autowired
    public ConnectionCreatedEventListener(ConnectionSyncUpFacade connectionSyncUpFacade,
                                          ProjectSyncUpFacade projectSyncUpFacade) {
        this.connectionSyncUpFacade = connectionSyncUpFacade;
        this.projectSyncUpFacade = projectSyncUpFacade;
    }

    @Async
    @EventListener
    public void onConnectionCreated(ConnectionCreatedEvent connCreatedEvent) {
        LOGGER.info("Handle onConnectionCreated");
        this.connectionSyncUpFacade.syncUpConnectionTables(connCreatedEvent.getConnection());
    }

    @Async
    @EventListener
    public void onTablesCreated(TableCreatedEvent tableCreatedEvent) {
        LOGGER.info("Handle onTableCreated");
        this.connectionSyncUpFacade.syncUpAllTableColumns(tableCreatedEvent.getConnection(), tableCreatedEvent.getTable());
    }

    @Async
    @EventListener
    public void onProjectCreated(ProjectCreatedEvent projectCreatedEvent) {
        LOGGER.info("Handle onProjectCreated");
        this.projectSyncUpFacade.syncUpProjectPlans(projectCreatedEvent.getProjectModel());
    }
}
