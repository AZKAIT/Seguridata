package com.seguridata.tools.dbmigrator.data.repository;

import com.seguridata.tools.dbmigrator.data.entity.PlanEntity;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PlanRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public PlanRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public PlanEntity createPlan(PlanEntity plan) {
        return this.mongoTemplate.insert(plan);
    }

    public PlanEntity updatePlan(PlanEntity plan) {
        return this.mongoTemplate.save(plan);
    }

    public PlanEntity getPlan(String planId) {
        return this.mongoTemplate.findById(planId, PlanEntity.class);
    }

    public List<PlanEntity> getPlansForProject(String projectId) {
        Criteria projectCriteria = Criteria.where("project").is(new ObjectId(projectId));
        return this.mongoTemplate.find(new Query(projectCriteria), PlanEntity.class);
    }

    public boolean deleteConnection(String id) {
        return this.mongoTemplate.remove(PlanEntity.class)
                .matching(Criteria.where("_id").is(new ObjectId(id)))
                .one()
                .getDeletedCount() > 0;
    }
}
