package com.seguridata.tools.dbmigrator.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import static com.seguridata.tools.dbmigrator.business.constant.TopicConstants.APP_SPECIFIC_PREFIX;
import static com.seguridata.tools.dbmigrator.business.constant.TopicConstants.QUEUES_PREFIX;
import static com.seguridata.tools.dbmigrator.business.constant.TopicConstants.TOPICS_PREFIX;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/notifications")
                .setAllowedOrigins("http://localhost:4200");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config){
        config.enableSimpleBroker(TOPICS_PREFIX, QUEUES_PREFIX);
        config.setApplicationDestinationPrefixes(APP_SPECIFIC_PREFIX);
    }
}
