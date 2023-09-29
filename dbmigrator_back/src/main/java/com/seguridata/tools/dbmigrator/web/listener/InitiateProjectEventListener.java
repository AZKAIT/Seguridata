package com.seguridata.tools.dbmigrator.web.listener;

import com.seguridata.tools.dbmigrator.business.event.InitiateProjectEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class InitiateProjectEventListener {
    @EventListener
    public void handleContextStart(InitiateProjectEvent initiateProjectEvent) {
        System.out.println("Handling context started event.");
    }
}
