package com.seguridata.tools.dbmigrator.business.service;

import com.seguridata.tools.dbmigrator.business.exception.DuplicateDataException;
import com.seguridata.tools.dbmigrator.business.exception.EmptyResultException;
import com.seguridata.tools.dbmigrator.business.exception.MissingObjectException;
import com.seguridata.tools.dbmigrator.data.entity.DefinitionEntity;
import com.seguridata.tools.dbmigrator.data.entity.PlanEntity;
import com.seguridata.tools.dbmigrator.data.repository.DefinitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

@Service
public class DefinitionService {

    private final DefinitionRepository definitionRepo;

    @Autowired
    public DefinitionService(DefinitionRepository definitionRepo) {
        this.definitionRepo = definitionRepo;
    }

    public DefinitionEntity createDefinition(PlanEntity plan, DefinitionEntity definition) {
        definition.setPlan(plan);

        return this.definitionRepo.createDefinition(definition);
    }

    public List<DefinitionEntity> getDefinitionsForPlan(String planId) {
        List<DefinitionEntity> definitions = this.definitionRepo.findDefinitionListByPlan(planId);
        if (CollectionUtils.isEmpty(definitions)) {
            throw new EmptyResultException("Empty definition list for plan");
        }

        return definitions;
    }

    public DefinitionEntity getDefinition(String definitionId) {
        DefinitionEntity definitionEntity = this.definitionRepo.findDefinition(definitionId);

        if (Objects.isNull(definitionEntity)) {
            throw new MissingObjectException("Definition doesn't exist");
        }

        return definitionEntity;
    }

    public DefinitionEntity updateDefinition(DefinitionEntity existingDefinition, DefinitionEntity updatedDefinition) {
        updatedDefinition.setId(existingDefinition.getId());
        updatedDefinition.setPlan(existingDefinition.getPlan());

        return this.definitionRepo.updateDefinition(updatedDefinition);
    }

    public List<DefinitionEntity> createDefinitionList(PlanEntity plan, List<DefinitionEntity> definitions) {
        definitions.forEach(col -> {
            col.setId(null);
            col.setPlan(plan);
        });

        return this.definitionRepo.createDefinitionList(definitions);
    }
}
