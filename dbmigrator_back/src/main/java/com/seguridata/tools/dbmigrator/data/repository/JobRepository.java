package com.seguridata.tools.dbmigrator.data.repository;

import com.mongodb.client.result.UpdateResult;
import com.seguridata.tools.dbmigrator.data.constant.JobStatus;
import com.seguridata.tools.dbmigrator.data.entity.JobEntity;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.seguridata.tools.dbmigrator.data.constant.JobStatus.FINISHED_ERROR;
import static com.seguridata.tools.dbmigrator.data.constant.JobStatus.FINISHED_SUCCESS;
import static com.seguridata.tools.dbmigrator.data.constant.JobStatus.FINISHED_WARN;
import static com.seguridata.tools.dbmigrator.data.constant.JobStatus.STARTING;
import static com.seguridata.tools.dbmigrator.data.constant.JobStatus.STOPPED;

@Repository
public class JobRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public JobRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public JobEntity createJob(JobEntity job) {
        return this.mongoTemplate.insert(job);
    }

    public JobEntity getJob(String id) {
        return this.mongoTemplate.findById(id, JobEntity.class);
    }

    public List<JobEntity> findJobsForProject(String projectId) {
        return this.mongoTemplate.find(Query.query(Criteria.where("project").is(new ObjectId(projectId))), JobEntity.class);
    }

    public List<JobEntity> getAllJobs() {
        Query query = new Query()
                .with(Sort.by(Sort.Direction.DESC, "createdAt"));
        return this.mongoTemplate.find(query, JobEntity.class);
    }

    public boolean updateJob(String jobId, JobStatus status) {
        Update statusUpdate = new Update()
                .set("status", status);

        if (STARTING.equals(status)) {
            statusUpdate = statusUpdate.set("startedAt", new Date());
        }

        if (Arrays.asList(STOPPED, FINISHED_SUCCESS, FINISHED_WARN, FINISHED_ERROR).contains(status)) {
            statusUpdate = statusUpdate.set("finishedAt", new Date());
        }

        UpdateResult updateResult = this.mongoTemplate.update(JobEntity.class)
                .matching(Criteria.where("_id").is(new ObjectId(jobId)))
                .apply(statusUpdate).all();

        return updateResult.getMatchedCount() == 1 && updateResult.getModifiedCount() == 1;
    }

    public JobEntity findLatestJobForProject(String projectId) {
        Query query = new Query(Criteria.where("project").is(new ObjectId(projectId)))
                .limit(1)
                .with(Sort.by(Sort.Direction.DESC, "createdAt"));

        return this.mongoTemplate.findOne(query, JobEntity.class);
    }

    public Long countProjectExecutions(String projectId) {
        return this.mongoTemplate.count(Query.query(Criteria.where("project").is(new ObjectId(projectId))), JobEntity.class);
    }
}
