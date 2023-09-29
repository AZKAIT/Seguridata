package com.seguridata.tools.dbmigrator.web.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

import java.util.Collection;
import java.util.Collections;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Override
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://60336155-dbd7-4f39-9be9-c4051fefacb1:ft0QdzOrN6cMKWLI@ccicluster.4wfpkq7.mongodb.net/?retryWrites=true&w=majority");
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();

        return MongoClients.create(mongoClientSettings);
    }

    @Override
    protected String getDatabaseName() {
        return "db_migration";
    }

    @Override
    public Collection<String> getMappingBasePackages() {
        return Collections.singleton("com.seguridata.tools.dbmigrator.data.entity");
    }

    @Override
    protected boolean autoIndexCreation() {
        return true;
    }
}
