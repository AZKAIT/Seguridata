package com.seguridata.tools.dbmigrator.data.repository;

import com.seguridata.tools.dbmigrator.data.entity.PlanEntity;
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
        return this.mongoTemplate.find(query(projectCriteria), PlanEntity.class);
    }

    public PlanEntity deletePlan(String id) {
        return this.mongoTemplate.findAndRemove(query(Criteria.where("_id").is(new ObjectId(id))), PlanEntity.class);
    }

    public List<PlanEntity> deletePlansByProjectIds(Collection<String> projectIds) {
        Criteria projectCriteria = Criteria.where("project").in(projectIds.stream().map(ObjectId::new).collect(Collectors.toList()));
        return this.mongoTemplate.findAllAndRemove(query(projectCriteria), PlanEntity.class);
    }

    public boolean planContainsTable(String tableId) {
        Criteria containsTable = Criteria.where("").orOperator(Criteria.where("sourceTable").is(new ObjectId(tableId)),
                Criteria.where("targetTable").is(new ObjectId(tableId)));
        return this.mongoTemplate.exists(query(containsTable), PlanEntity.class);
    }
}
