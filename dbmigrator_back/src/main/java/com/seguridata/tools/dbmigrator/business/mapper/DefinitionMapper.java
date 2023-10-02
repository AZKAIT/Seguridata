package com.seguridata.tools.dbmigrator.business.mapper;

import com.seguridata.tools.dbmigrator.data.constant.ConversionFunction;
import com.seguridata.tools.dbmigrator.data.entity.DefinitionEntity;
import com.seguridata.tools.dbmigrator.data.model.DefinitionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class DefinitionMapper {

    private final ColumnMapper columnMapper;

    @Autowired
    public DefinitionMapper(ColumnMapper columnMapper) {
        this.columnMapper = columnMapper;
    }

    public List<DefinitionEntity> mapDefinitionEntityList(Collection<DefinitionModel> definitions) {
        return definitions.stream().filter(Objects::nonNull).map(this::mapDefinitionEntity).collect(Collectors.toList());
    }

    public List<DefinitionModel> mapDefinitionModelList(Collection<DefinitionEntity> definitions) {
        return definitions.stream().filter(Objects::nonNull).map(this::mapDefinitionModel).collect(Collectors.toList());
    }


    public DefinitionEntity mapDefinitionEntity(DefinitionModel definitionModel) {
        if (Objects.isNull(definitionModel)) {
            return null;
        }

        DefinitionEntity definition = new DefinitionEntity();
        definition.setId(definitionModel.getId());
        definition.setSourceColumn(this.columnMapper.mapColumnEntity(definitionModel.getSourceColumn()));
        definition.setTargetColumn(this.columnMapper.mapColumnEntity(definitionModel.getTargetColumn()));
        definition.setConversionFunction(Optional.ofNullable(definitionModel.getConversionFunction()).orElse(ConversionFunction.NONE));

        return definition;
    }

    public DefinitionModel mapDefinitionModel(DefinitionEntity definition) {
        if (Objects.isNull(definition)) {
            return null;
        }

        DefinitionModel definitionModel = new DefinitionModel();
        definitionModel.setId(definition.getId());
        definitionModel.setConversionFunction(definition.getConversionFunction());
        definitionModel.setSourceColumn(this.columnMapper.mapColumnModel(definition.getSourceColumn()));
        definitionModel.setTargetColumn(this.columnMapper.mapColumnModel(definition.getTargetColumn()));

        return definitionModel;
    }
}
