package com.seguridata.tools.dbmigrator.business.mapper;

import com.seguridata.tools.dbmigrator.data.entity.DefinitionEntity;
import com.seguridata.tools.dbmigrator.data.entity.PlanEntity;
import com.seguridata.tools.dbmigrator.data.model.DefinitionModel;
import com.seguridata.tools.dbmigrator.data.model.PlanModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class PlanMapper {

    private final TableMapper tableMapper;

    @Autowired
    public PlanMapper(TableMapper tableMapper) {
        this.tableMapper = tableMapper;
    }

    public List<PlanModel> mapPlanModelList(Collection<PlanEntity> plans) {
        return plans.stream().filter(Objects::nonNull).map(this::mapPlanModel).collect(Collectors.toList());
    }

    public PlanEntity mapPlanEntity(PlanModel planModel) {
        PlanEntity plan = new PlanEntity();
        plan.setId(planModel.getId());
        plan.setOrderNum(planModel.getOrderNum());
        plan.setStartDate(planModel.getStartDate());
        plan.setEndDate(planModel.getEndDate());
        plan.setStartId(planModel.getStartId());
        plan.setEndId(planModel.getEndId());
        plan.setIdList(planModel.getIdList());
        plan.setProgressPercent(planModel.getProgressPercent());
        plan.setStatus(planModel.getStatus());

        plan.setSourceTable(this.tableMapper.mapTableEntity(planModel.getSourceTable()));
        plan.setTargetTable(this.tableMapper.mapTableEntity(planModel.getTargetTable()));

        return plan;
    }

    public PlanModel mapPlanModel(PlanEntity plan) {
        PlanModel planModel = new PlanModel();
        planModel.setId(plan.getId());
        planModel.setOrderNum(plan.getOrderNum());
        planModel.setStartDate(plan.getStartDate());
        planModel.setEndDate(plan.getEndDate());
        planModel.setStartId(plan.getStartId());
        planModel.setEndId(plan.getEndId());
        planModel.setIdList(plan.getIdList());
        planModel.setProgressPercent(plan.getProgressPercent());
        planModel.setStatus(plan.getStatus());

        planModel.setSourceTable(this.tableMapper.mapTableModel(plan.getSourceTable()));
        planModel.setTargetTable(this.tableMapper.mapTableModel(plan.getTargetTable()));

        return planModel;
    }
}
