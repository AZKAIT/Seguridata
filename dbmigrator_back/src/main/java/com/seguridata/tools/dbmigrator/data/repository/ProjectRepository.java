package com.seguridata.tools.dbmigrator.data.repository;

import com.mongodb.client.result.UpdateResult;
import com.seguridata.tools.dbmigrator.data.constant.ProjectStatus;
import com.seguridata.tools.dbmigrator.data.entity.ProjectEntity;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class ProjectRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public ProjectRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public ProjectEntity createProject(ProjectEntity project) {
        return this.mongoTemplate.insert(project);
    }

    public ProjectEntity updateProject(ProjectEntity project) {
        return this.mongoTemplate.save(project);
    }

    public ProjectEntity getProject(String projectId) {
        return this.mongoTemplate.findById(projectId, ProjectEntity.class);
    }

    public List<ProjectEntity> getAllProjects() {
        return this.mongoTemplate.findAll(ProjectEntity.class);
    }

    public ProjectEntity deleteProject(String id) {
        return this.mongoTemplate.findAndRemove(new Query(Criteria.where("_id").is(new ObjectId(id))), ProjectEntity.class);
    }

    public boolean updateProjectStatus(String projectId, ProjectStatus status) {
        UpdateResult updateResult = this.mongoTemplate.update(ProjectEntity.class)
                .matching(Criteria.where("_id").is(new ObjectId(projectId)))
                .apply(new Update().set("status", status).set("lastStatusDate", new Date())).all();

        return updateResult.getMatchedCount() == 1 && updateResult.getModifiedCount() == 1;
    }
}
