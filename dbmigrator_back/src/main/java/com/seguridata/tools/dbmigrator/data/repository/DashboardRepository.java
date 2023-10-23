package com.seguridata.tools.dbmigrator.data.repository;

import com.seguridata.tools.dbmigrator.data.dto.DashboardDataDTO;
import com.seguridata.tools.dbmigrator.data.entity.ConnectionEntity;
import com.seguridata.tools.dbmigrator.data.entity.ProjectEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class DashboardRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public DashboardRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public DashboardDataDTO executeDashboardDataAggregation() {
        Long connCount = this.mongoTemplate.count(new Query(), ConnectionEntity.class);
        Long projCount = this.mongoTemplate.count(new Query(), ProjectEntity.class);

        DashboardDataDTO dashboardData = new DashboardDataDTO();
        dashboardData.setConnectionTotal(connCount);
        dashboardData.setProjectTotal(projCount);
        return dashboardData;
    }
}
