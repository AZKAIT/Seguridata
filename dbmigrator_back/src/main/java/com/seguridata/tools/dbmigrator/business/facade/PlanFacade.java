package com.seguridata.tools.dbmigrator.business.facade;

import com.seguridata.tools.dbmigrator.business.exception.BaseCodeException;
import com.seguridata.tools.dbmigrator.business.mapper.PlanMapper;
import com.seguridata.tools.dbmigrator.business.service.PlanService;
import com.seguridata.tools.dbmigrator.business.service.ProjectService;
import com.seguridata.tools.dbmigrator.business.service.TableService;
import com.seguridata.tools.dbmigrator.data.entity.PlanEntity;
import com.seguridata.tools.dbmigrator.data.entity.ProjectEntity;
import com.seguridata.tools.dbmigrator.data.model.PlanModel;
import com.seguridata.tools.dbmigrator.data.wrapper.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class PlanFacade {

    private final ProjectService projectService;
    private final PlanService planService;
    private final TableService tableService;
    private final PlanMapper planMapper;

    @Autowired
    public PlanFacade(ProjectService projectService,
                      PlanService planService,
                      TableService tableService,
                      PlanMapper planMapper) {
        this.projectService = projectService;
        this.planService = planService;
        this.tableService = tableService;
        this.planMapper = planMapper;
    }

    public ResponseWrapper<PlanModel> createPlanForProject(String projectId, PlanModel planModel) {
        ResponseWrapper<PlanModel> planResponse = new ResponseWrapper<>();
        try {
            ProjectEntity project = this.projectService.getProject(projectId);
            this.projectService.validateProjectStatus(project);

            PlanEntity plan = this.planMapper.mapPlanEntity(planModel);
            this.validateTables(plan);
            plan = this.planService.createPlanForProject(project, plan);

            planResponse.setCode("00");
            planResponse.setData(this.planMapper.mapPlanModel(plan));
        } catch (BaseCodeException e) {
            planResponse.setCode(e.getCode());
            planResponse.setMessages(Arrays.asList(e.getMessages()));
        }
        return planResponse;
    }

    public ResponseWrapper<PlanModel> getPlan(String planId) {
        ResponseWrapper<PlanModel> planResponse = new ResponseWrapper<>();
        try {
            PlanEntity plan = this.planService.getPlan(planId);

            planResponse.setCode("00");
            planResponse.setData(this.planMapper.mapPlanModel(plan));
        } catch (BaseCodeException e) {
            planResponse.setCode(e.getCode());
            planResponse.setMessages(Arrays.asList(e.getMessages()));
        }
        return planResponse;
    }

    public ResponseWrapper<List<PlanModel>> getPlansForProject(String projectId) {
        ResponseWrapper<List<PlanModel>> planResponse = new ResponseWrapper<>();
        try {
            List<PlanEntity> plans = this.planService.getPlansForProject(projectId);

            planResponse.setCode("00");
            planResponse.setData(this.planMapper.mapPlanModelList(plans));
        } catch (BaseCodeException e) {
            planResponse.setCode(e.getCode());
            planResponse.setMessages(Arrays.asList(e.getMessages()));
        }
        return planResponse;
    }

    public ResponseWrapper<PlanModel> updatePlan(String planId, PlanModel planModel) {
        ResponseWrapper<PlanModel> planResponse = new ResponseWrapper<>();
        try {
            PlanEntity existingPlan = this.planService.getPlan(planId);
            this.projectService.validateProjectStatus(existingPlan.getProject());

            PlanEntity updatedPlan = this.planMapper.mapPlanEntity(planModel);
            this.validateTables(updatedPlan);
            updatedPlan = this.planService.updatePlan(existingPlan, updatedPlan);

            planResponse.setCode("00");
            planResponse.setData(this.planMapper.mapPlanModel(updatedPlan));
        } catch (BaseCodeException e) {
            planResponse.setCode(e.getCode());
            planResponse.setMessages(Arrays.asList(e.getMessages()));
        }
        return planResponse;
    }

    private void validateTables(PlanEntity plan) {
        this.tableService.getTable(plan.getSourceTable().getId());
        this.tableService.getTable(plan.getTargetTable().getId());
    }
}
