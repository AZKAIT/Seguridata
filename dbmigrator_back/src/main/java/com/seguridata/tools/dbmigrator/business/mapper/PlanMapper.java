package com.seguridata.tools.dbmigrator.business.mapper;

import com.seguridata.tools.dbmigrator.data.entity.PlanEntity;
import com.seguridata.tools.dbmigrator.data.model.PlanModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        plan.setInitialSkip(planModel.getInitialSkip());
        plan.setRowLimit(planModel.getRowLimit());
        plan.setMaxRows(planModel.getMaxRows());
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
        planModel.setInitialSkip(plan.getInitialSkip());
        planModel.setRowLimit(plan.getRowLimit());
        planModel.setMaxRows(plan.getMaxRows());
        planModel.setProgressPercent(plan.getProgressPercent());
        planModel.setStatus(plan.getStatus());

        planModel.setSourceTable(this.tableMapper.mapTableModel(plan.getSourceTable()));
        planModel.setTargetTable(this.tableMapper.mapTableModel(plan.getTargetTable()));

        return planModel;
    }
}
