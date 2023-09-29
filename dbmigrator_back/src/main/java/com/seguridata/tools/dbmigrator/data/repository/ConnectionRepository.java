package com.seguridata.tools.dbmigrator.data.repository;

import com.seguridata.tools.dbmigrator.data.entity.ConnectionEntity;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ConnectionRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public ConnectionRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    public ConnectionEntity insertConnection(ConnectionEntity connection) {
        return this.mongoTemplate.insert(ConnectionEntity.class).one(connection);
    }

    public ConnectionEntity updateConnection(ConnectionEntity connection) {
        return this.mongoTemplate.save(connection);
    }

    public ConnectionEntity getConnection(String id) {
        return this.mongoTemplate.findById(id, ConnectionEntity.class);
    }

    public List<ConnectionEntity> getConnectionList() {
        return this.mongoTemplate.findAll(ConnectionEntity.class);
    }

    public boolean deleteConnection(String id) {
        return this.mongoTemplate.remove(ConnectionEntity.class)
                .matching(Criteria.where("_id").is(new ObjectId(id)))
                .one()
                .getDeletedCount() > 0;
    }

    public boolean validateConnectionData(ConnectionEntity connection) {
        Criteria existCriteria = Criteria.where("host").is(connection.getHost())
                .and("port").is(connection.getPort())
                .and("database").is(connection.getDatabase());

        return this.mongoTemplate.exists(new Query(existCriteria), ConnectionEntity.class);
    }

    public boolean connectionExists(String id) {
        return this.mongoTemplate.exists(new Query(Criteria.where("_id").is(new ObjectId(id))), ConnectionEntity.class);
    }
}
