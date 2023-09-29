package com.seguridata.tools.dbmigrator.data.repository;

import com.seguridata.tools.dbmigrator.data.entity.ColumnEntity;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ColumnRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public ColumnRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public ColumnEntity createColumn(ColumnEntity column) {
        return this.mongoTemplate.insert(column);
    }

    public ColumnEntity findColumn(String columnId) {
        return this.mongoTemplate.findById(columnId, ColumnEntity.class);
    }

    public List<ColumnEntity> findColumnListByTable(String tableId) {
        Criteria tableCriteria = Criteria.where("table").is(new ObjectId(tableId));
        return this.mongoTemplate.find(new Query(tableCriteria), ColumnEntity.class);
    }

    public ColumnEntity updateColumn(ColumnEntity column) {
        return this.mongoTemplate.save(column);
    }

    public boolean deleteColumn(String id) {
        return this.mongoTemplate.remove(ColumnEntity.class)
                .matching(Criteria.where("_id").is(new ObjectId(id)))
                .one()
                .getDeletedCount() > 0;
    }


    public boolean validateColumnData(String tableId, ColumnEntity column) {
        Criteria existCriteria = Criteria.where("table").is(new ObjectId(tableId))
                .and("name").is(column.getName());

        return this.mongoTemplate.exists(new Query(existCriteria), ColumnEntity.class);
    }

    public boolean validateColumnTable(String tableId, String columnId) {
        Criteria existCriteria = Criteria.where("table").is(new ObjectId(tableId))
                .and("_id").is(new ObjectId(columnId));

        return this.mongoTemplate.exists(new Query(existCriteria), ColumnEntity.class);
    }

    public List<ColumnEntity> createColumnList(List<ColumnEntity> columnList) {
        return new ArrayList<>(this.mongoTemplate.insert(columnList, ColumnEntity.class));
    }

    public List<ColumnEntity> findSortingColumnForTable(String tableId) {
        Criteria sortColCriteria = Criteria.where("table").is(new ObjectId(tableId))
                .and("sorting").is(true);
        return this.mongoTemplate.find(new Query(sortColCriteria), ColumnEntity.class);
    }
}
