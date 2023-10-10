package com.seguridata.tools.dbmigrator.business.service;

import com.seguridata.tools.dbmigrator.business.exception.EmptyResultException;
import com.seguridata.tools.dbmigrator.business.exception.MissingObjectException;
import com.seguridata.tools.dbmigrator.data.entity.PlanEntity;
import com.seguridata.tools.dbmigrator.data.entity.ProjectEntity;
import com.seguridata.tools.dbmigrator.data.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

@Service
public class PlanService {

    private final PlanRepository planRepo;

    @Autowired
    public PlanService(PlanRepository planRepo) {
        this.planRepo = planRepo;
    }

    public List<PlanEntity> getPlansForProject(String projectId) {
        List<PlanEntity> plans = this.planRepo.getPlansForProject(projectId);
        if (CollectionUtils.isEmpty(plans)) {
            throw new EmptyResultException("No plans found for given project");
        }

        return plans;
    }

    public PlanEntity getPlan(String planId) {
        PlanEntity plan = this.planRepo.getPlan(planId);
        if (Objects.isNull(plan)) {
            throw new MissingObjectException("Plan not found");
        }

        return plan;
    }

    public PlanEntity createPlanForProject(ProjectEntity project, PlanEntity plan) {
        plan.setId(null);
        plan.setProject(project);

        if (Objects.isNull(plan.getMaxRows()) || Objects.equals(0L, plan.getMaxRows())) {
            plan.setMaxRows(-1L);
        }

        return this.planRepo.createPlan(plan);
    }

    public PlanEntity updatePlan(PlanEntity existingPlan, PlanEntity updatedPlan) {
        updatedPlan.setId(existingPlan.getId());
        updatedPlan.setProgressPercent(existingPlan.getProgressPercent());
        updatedPlan.setStatus(existingPlan.getStatus());
        updatedPlan.setStartDate(existingPlan.getStartDate());
        updatedPlan.setEndDate(existingPlan.getEndDate());
        updatedPlan.setProject(existingPlan.getProject());

        if (Objects.isNull(updatedPlan.getMaxRows()) || Objects.equals(0L, updatedPlan.getMaxRows())) {
            updatedPlan.setMaxRows(-1L);
        }

        return this.planRepo.updatePlan(updatedPlan);
    }

    public PlanEntity deletePlan(PlanEntity plan) {
        return this.planRepo.deletePlan(plan.getId());
    }
}
