package com.seguridata.tools.dbmigrator.business.facade;

import com.seguridata.tools.dbmigrator.business.exception.BaseCodeException;
import com.seguridata.tools.dbmigrator.business.exception.EmptyResultException;
import com.seguridata.tools.dbmigrator.business.exception.MissingObjectException;
import com.seguridata.tools.dbmigrator.business.mapper.DefinitionMapper;
import com.seguridata.tools.dbmigrator.business.service.ColumnService;
import com.seguridata.tools.dbmigrator.business.service.DefinitionService;
import com.seguridata.tools.dbmigrator.business.service.PlanService;
import com.seguridata.tools.dbmigrator.business.service.ProjectService;
import com.seguridata.tools.dbmigrator.data.entity.DefinitionEntity;
import com.seguridata.tools.dbmigrator.data.entity.PlanEntity;
import com.seguridata.tools.dbmigrator.data.model.DefinitionModel;
import com.seguridata.tools.dbmigrator.data.wrapper.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;


@Component
public class DefinitionFacade {

    private final ProjectService projectService;
    private final ColumnService columnService;
    private final DefinitionService definitionService;
    private final PlanService planService;
    private final DefinitionMapper definitionMapper;

    @Autowired
    public DefinitionFacade(ProjectService projectService,
                            ColumnService columnService,
                            DefinitionService definitionService,
                            PlanService planService,
                            DefinitionMapper definitionMapper) {
        this.projectService = projectService;
        this.columnService = columnService;
        this.definitionService = definitionService;
        this.planService = planService;
        this.definitionMapper = definitionMapper;
    }


    public ResponseWrapper<List<DefinitionModel>> getPlanDefinitions(String planId) {
        ResponseWrapper<List<DefinitionModel>> response = new ResponseWrapper<>();
        try {
            List<DefinitionEntity> definitions = this.definitionService.getDefinitionsForPlan(planId);

            response.setCode("00");
            response.setData(this.definitionMapper.mapDefinitionModelList(definitions));
        } catch (BaseCodeException e) {
            response.setCode(e.getCode());
            response.setMessages(Arrays.asList(e.getMessages()));
        }
        return response;
    }

    public ResponseWrapper<DefinitionModel> createPlanDefinition(String planId,
                                                                 DefinitionModel definitionModel) {
        ResponseWrapper<DefinitionModel> response = new ResponseWrapper<>();
        try {
            PlanEntity plan = this.planService.getPlan(planId);
            this.projectService.validateProjectStatus(plan.getProject());

            DefinitionEntity definition = this.definitionMapper.mapDefinitionEntity(definitionModel);
            this.validateColumns(definition);
            definition = this.definitionService.createDefinition(plan, definition);

            response.setCode("00");
            response.setData(this.definitionMapper.mapDefinitionModel(definition));
        } catch (BaseCodeException e) {
            response.setCode(e.getCode());
            response.setMessages(Arrays.asList(e.getMessages()));
        }
        return response;
    }

    public ResponseWrapper<List<DefinitionModel>> createPlanDefinitionList(String planId,
                                                                           List<DefinitionModel> definitionModelList) {
        ResponseWrapper<List<DefinitionModel>> response = new ResponseWrapper<>();
        try {
            PlanEntity plan = this.planService.getPlan(planId);
            this.projectService.validateProjectStatus(plan.getProject());

            List<DefinitionEntity> definitionList = this.definitionMapper.mapDefinitionEntityList(definitionModelList);
            if (CollectionUtils.isEmpty(definitionList)) {
                throw new EmptyResultException("Input Column list is empty");
            }
            definitionList.forEach(this::validateColumns);

            definitionList = this.definitionService.createDefinitionList(plan, definitionList);

            response.setCode("00");
            response.setData(this.definitionMapper.mapDefinitionModelList(definitionList));
        } catch (BaseCodeException e) {
            response.setCode(e.getCode());
            response.setMessages(Arrays.asList(e.getMessages()));
        }
        return response;
    }

    public ResponseWrapper<DefinitionModel> getDefinition(String definitionId) {
        ResponseWrapper<DefinitionModel> response = new ResponseWrapper<>();
        try {
            DefinitionEntity definition = this.definitionService.getDefinition(definitionId);

            response.setCode("00");
            response.setData(this.definitionMapper.mapDefinitionModel(definition));
        } catch (BaseCodeException e) {
            response.setCode(e.getCode());
            response.setMessages(Arrays.asList(e.getMessages()));
        }
        return response;
    }

    public ResponseWrapper<DefinitionModel> updateDefinition(String definitionId,
                                                             DefinitionModel definitionModel) {
        ResponseWrapper<DefinitionModel> response = new ResponseWrapper<>();
        try {
            DefinitionEntity existingDefinition = this.definitionService.getDefinition(definitionId);
            PlanEntity plan = existingDefinition.getPlan();
            if (Objects.isNull(plan)) {
                throw new MissingObjectException("Plan not found for column");
            }

            this.projectService.validateProjectStatus(plan.getProject());

            DefinitionEntity updatedDefinition = this.definitionMapper.mapDefinitionEntity(definitionModel);
            this.validateColumns(updatedDefinition);
            updatedDefinition = this.definitionService.updateDefinition(existingDefinition, updatedDefinition);

            response.setCode("00");
            response.setData(this.definitionMapper.mapDefinitionModel(updatedDefinition));
        } catch (BaseCodeException e) {
            response.setCode(e.getCode());
            response.setMessages(Arrays.asList(e.getMessages()));
        }
        return response;
    }

    private void validateColumns(DefinitionEntity definition) {
        this.columnService.getColumn(definition.getSourceColumn().getId());
        this.columnService.getColumn(definition.getTargetColumn().getId());
    }
}
