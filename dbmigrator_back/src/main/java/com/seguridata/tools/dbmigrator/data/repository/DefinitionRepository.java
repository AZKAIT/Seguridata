package com.seguridata.tools.dbmigrator.data.repository;

import com.seguridata.tools.dbmigrator.data.entity.DefinitionEntity;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class DefinitionRepository {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public DefinitionRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    public DefinitionEntity createDefinition(DefinitionEntity definition) {
        return this.mongoTemplate.insert(definition);
    }

    public DefinitionEntity findDefinition(String definitionId) {
        return this.mongoTemplate.findById(definitionId, DefinitionEntity.class);
    }

    public List<DefinitionEntity> findDefinitionListByPlan(String planId) {
        Criteria planCriteria = Criteria.where("plan").is(new ObjectId(planId));
        return this.mongoTemplate.find(new Query(planCriteria), DefinitionEntity.class);
    }

    public DefinitionEntity updateDefinition(DefinitionEntity definition) {
        return this.mongoTemplate.save(definition);
    }

    public boolean deleteDefinition(String id) {
        return this.mongoTemplate.remove(DefinitionEntity.class)
                .matching(Criteria.where("_id").is(new ObjectId(id)))
                .one()
                .getDeletedCount() > 0;
    }

    public List<DefinitionEntity> createDefinitionList(List<DefinitionEntity> definitionList) {
        return new ArrayList<>(this.mongoTemplate.insert(definitionList, DefinitionEntity.class));
    }
}
