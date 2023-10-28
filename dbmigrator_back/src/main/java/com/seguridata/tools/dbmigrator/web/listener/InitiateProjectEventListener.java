package com.seguridata.tools.dbmigrator.web.listener;

import com.seguridata.tools.dbmigrator.business.event.InitiateProjectEvent;
import com.seguridata.tools.dbmigrator.business.event.StopProjectEvent;
import com.seguridata.tools.dbmigrator.business.facade.ExecutionFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class InitiateProjectEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(InitiateProjectEventListener.class);

    private final ExecutionFacade executionFacade;

    @Autowired
    public InitiateProjectEventListener(ExecutionFacade executionFacade) {
        this.executionFacade = executionFacade;
    }

    @Async
    @EventListener
    public void handleProjectExecStart(InitiateProjectEvent initiateProjectEvent) {
        LOGGER.debug("On handleContextStart");
        this.executionFacade.startExecution(initiateProjectEvent.getJob());
    }

    @Async
    @EventListener
    public void handleProjectExecStop(StopProjectEvent stopProjectEvent) {
        LOGGER.debug("On handleProjectExecStop");
        this.executionFacade.stopExecution(stopProjectEvent.getJob());
    }
}
