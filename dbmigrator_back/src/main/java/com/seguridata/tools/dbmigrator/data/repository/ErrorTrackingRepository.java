package com.seguridata.tools.dbmigrator.data.repository;

import com.seguridata.tools.dbmigrator.data.entity.ErrorTrackingEntity;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ErrorTrackingRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public ErrorTrackingRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public ErrorTrackingEntity createErrorTracking(ErrorTrackingEntity errorTracking) {
        return this.mongoTemplate.insert(errorTracking);
    }

    public List<ErrorTrackingEntity> findErrorTrackingForProject(String projectId) {
        CriteriaDefinition projectCriteria = Criteria.where("project").is(new ObjectId(projectId));
        return this.mongoTemplate.find(new Query(projectCriteria), ErrorTrackingEntity.class);
    }
}
