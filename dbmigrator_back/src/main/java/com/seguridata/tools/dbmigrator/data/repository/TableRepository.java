package com.seguridata.tools.dbmigrator.data.repository;

import com.seguridata.tools.dbmigrator.data.entity.TableEntity;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
public class TableRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public TableRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public TableEntity createTable(TableEntity table) {
        return this.mongoTemplate.insert(TableEntity.class).one(table);
    }

    public TableEntity getTable(String id) {
        return this.mongoTemplate.findById(id, TableEntity.class);
    }

    public List<TableEntity> getTableListByConnection(String connectionId) {
        Criteria connectionCriteria = Criteria.where("connection").is(new ObjectId(connectionId));
        return this.mongoTemplate.find(query(connectionCriteria), TableEntity.class);
    }

    public TableEntity updateTable(TableEntity table) {
        return this.mongoTemplate.save(table);
    }

    public TableEntity deleteTable(String id) {
        return this.mongoTemplate.findAndRemove(query(Criteria.where("_id").is(new ObjectId(id))), TableEntity.class);
    }

    public boolean validateTableData(String connectionId, TableEntity table) {
        Criteria existCriteria = Criteria.where("connection").is(new ObjectId(connectionId))
                .and("schema").is(table.getSchema())
                .and("name").is(table.getName());

        return this.mongoTemplate.exists(query(existCriteria), TableEntity.class);
    }

    public List<TableEntity> deleteTablesByConnection(Collection<String> connectionIds) {
        Criteria connectionCriteria = Criteria.where("connection")
                .in(connectionIds.stream().map(ObjectId::new).collect(Collectors.toList()));
        return this.mongoTemplate.findAllAndRemove(query(connectionCriteria), TableEntity.class);
    }

    public boolean validateTableConnection(String connectionId, String tableId) {
        Criteria existCriteria = Criteria.where("connection").is(new ObjectId(connectionId))
                .and("_id").is(new ObjectId(tableId));

        return this.mongoTemplate.exists(query(existCriteria), TableEntity.class);
    }
}
