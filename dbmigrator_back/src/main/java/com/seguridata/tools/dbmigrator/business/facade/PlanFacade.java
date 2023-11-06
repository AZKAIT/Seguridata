package com.seguridata.tools.dbmigrator.business.facade;

import com.seguridata.tools.dbmigrator.business.exception.BaseCodeException;
import com.seguridata.tools.dbmigrator.business.mapper.PlanMapper;
import com.seguridata.tools.dbmigrator.business.service.DefinitionService;
import com.seguridata.tools.dbmigrator.business.service.PlanService;
import com.seguridata.tools.dbmigrator.business.service.ProjectService;
import com.seguridata.tools.dbmigrator.business.service.TableService;
import com.seguridata.tools.dbmigrator.data.entity.ConnectionEntity;
import com.seguridata.tools.dbmigrator.data.entity.PlanEntity;
import com.seguridata.tools.dbmigrator.data.entity.ProjectEntity;
import com.seguridata.tools.dbmigrator.data.entity.TableEntity;
import com.seguridata.tools.dbmigrator.data.model.PlanModel;
import com.seguridata.tools.dbmigrator.data.wrapper.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
public class PlanFacade {

    private final ProjectService projectService;
    private final PlanService planService;
    private final TableService tableService;
    private final DefinitionService definitionService;
    private final PlanMapper planMapper;

    @Autowired
    public PlanFacade(ProjectService projectService,
                      PlanService planService,
                      TableService tableService,
                      DefinitionService definitionService,
                      PlanMapper planMapper) {
        this.projectService = projectService;
        this.planService = planService;
        this.tableService = tableService;
        this.definitionService = definitionService;
        this.planMapper = planMapper;
    }

    public ResponseWrapper<PlanModel> createPlanForProject(String projectId, PlanModel planModel) {
        ResponseWrapper<PlanModel> planResponse = new ResponseWrapper<>();
        try {
            ProjectEntity project = this.projectService.getProject(projectId);
            this.projectService.validateProjectStatus(project);

            PlanEntity plan = this.planMapper.mapPlanEntity(planModel);
            this.validateTables(plan, project);
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

            ProjectEntity project = existingPlan.getProject();
            this.projectService.validateProjectStatus(project);

            PlanEntity updatedPlan = this.planMapper.mapPlanEntity(planModel);
            this.validateTables(updatedPlan, project);

            if (!Objects.equals(existingPlan.getSourceTable().getId(), updatedPlan.getSourceTable().getId())
                    || !Objects.equals(existingPlan.getTargetTable().getId(), updatedPlan.getTargetTable().getId())) {
                this.definitionService.deleteDefinitionsByPlan(existingPlan);
            }

            updatedPlan = this.planService.updatePlan(existingPlan, updatedPlan);

            planResponse.setCode("00");
            planResponse.setData(this.planMapper.mapPlanModel(updatedPlan));
        } catch (BaseCodeException e) {
            planResponse.setCode(e.getCode());
            planResponse.setMessages(Arrays.asList(e.getMessages()));
        }
        return planResponse;
    }

    public ResponseWrapper<PlanModel> deletePlan(String planId) {
        ResponseWrapper<PlanModel> planResponse = new ResponseWrapper<>();
        try {
            PlanEntity existingPlan = this.planService.getPlan(planId);
            this.projectService.validateProjectStatus(existingPlan.getProject());

            PlanEntity deletedPlan = this.planService.deletePlan(existingPlan);
            this.definitionService.deleteDefinitionsByPlan(deletedPlan);

            planResponse.setCode("00");
            planResponse.setData(this.planMapper.mapPlanModel(deletedPlan));
        } catch (BaseCodeException e) {
            planResponse.setCode(e.getCode());
            planResponse.setMessages(Arrays.asList(e.getMessages()));
        }
        return planResponse;
    }

    private void validateTables(PlanEntity plan, ProjectEntity project) {
        ConnectionEntity sourceConn = project.getSourceConnection();
        TableEntity sourceTable = plan.getSourceTable();
        this.tableService.validateTableOwner(sourceConn, sourceTable);


        ConnectionEntity targetConn = project.getTargetConnection();
        TableEntity targetTable = plan.getTargetTable();
        this.tableService.validateTableOwner(targetConn, targetTable);
    }
}
