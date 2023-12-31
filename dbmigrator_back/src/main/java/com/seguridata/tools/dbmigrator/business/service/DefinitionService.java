package com.seguridata.tools.dbmigrator.business.service;

import com.seguridata.tools.dbmigrator.business.exception.EmptyResultException;
import com.seguridata.tools.dbmigrator.business.exception.MissingObjectException;
import com.seguridata.tools.dbmigrator.business.exception.ObjectLockedException;
import com.seguridata.tools.dbmigrator.data.constant.ConversionFunctionType;
import com.seguridata.tools.dbmigrator.data.entity.ColumnEntity;
import com.seguridata.tools.dbmigrator.data.entity.DefinitionEntity;
import com.seguridata.tools.dbmigrator.data.entity.PlanEntity;
import com.seguridata.tools.dbmigrator.data.repository.DefinitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DefinitionService {

    private final DefinitionRepository definitionRepo;

    @Autowired
    public DefinitionService(DefinitionRepository definitionRepo) {
        this.definitionRepo = definitionRepo;
    }

    public DefinitionEntity createDefinition(PlanEntity plan, DefinitionEntity definition) {
        definition.setPlan(plan);
        if (Objects.isNull(definition.getConversionFunction())) {
            definition.setConversionFunction(ConversionFunctionType.NONE);
        }

        return this.definitionRepo.createDefinition(definition);
    }

    public List<DefinitionEntity> getDefinitionsForPlan(String planId) {
        List<DefinitionEntity> definitions = this.definitionRepo.findDefinitionListByPlan(planId);
        if (CollectionUtils.isEmpty(definitions)) {
            throw new EmptyResultException("La lista de Definiciones para este Plan está vacía");
        }

        return definitions;
    }

    public DefinitionEntity getDefinition(String definitionId) {
        DefinitionEntity definitionEntity = this.definitionRepo.findDefinition(definitionId);

        if (Objects.isNull(definitionEntity)) {
            throw new MissingObjectException("La Definición no existe");
        }

        return definitionEntity;
    }

    public DefinitionEntity updateDefinition(DefinitionEntity existingDefinition, DefinitionEntity updatedDefinition) {
        updatedDefinition.setId(existingDefinition.getId());
        updatedDefinition.setPlan(existingDefinition.getPlan());

        return this.definitionRepo.updateDefinition(updatedDefinition);
    }

    public List<DefinitionEntity> createDefinitionList(PlanEntity plan, List<DefinitionEntity> definitions) {
        definitions.forEach(def -> {
            def.setId(null);
            def.setPlan(plan);
            if (Objects.isNull(def.getConversionFunction())) {
                def.setConversionFunction(ConversionFunctionType.NONE);
            }
        });

        return this.definitionRepo.createDefinitionList(definitions);
    }

    public DefinitionEntity deleteDefinition(DefinitionEntity definition) {
        return this.definitionRepo.deleteDefinition(definition.getId());
    }

    public List<DefinitionEntity> deleteDefinitionsByPlan(PlanEntity plan) {
        return this.definitionRepo.deleteDefinitionsByPlanIds(Collections.singleton(plan.getId()));
    }

    public List<DefinitionEntity> deleteDefinitionsByPlanList(List<PlanEntity> plans) {
        List<String> planIds = plans.stream()
                .map(PlanEntity::getId)
                .collect(Collectors.toList());
        return this.definitionRepo.deleteDefinitionsByPlanIds(planIds);
    }

    public void defContainsColumn(ColumnEntity column) {
        if (this.definitionRepo.defContainsColumn(column.getId())) {
            throw new ObjectLockedException("La Columna está asociada a una Definición, no se puede eliminar");
        }
    }

    public List<DefinitionEntity> saveDefListForProject(PlanEntity plan, List<DefinitionEntity> newDefinitions) {
        newDefinitions.forEach(def -> {
            def.setId(null);
            def.setPlan(plan);
            def.setConversionFunction(ConversionFunctionType.NONE);
        });

        return this.definitionRepo.saveBatch(newDefinitions);
    }
}
