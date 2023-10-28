package com.seguridata.tools.dbmigrator.business.mapper;

import com.seguridata.tools.dbmigrator.data.entity.ErrorTrackingEntity;
import com.seguridata.tools.dbmigrator.data.model.ErrorTrackingModel;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ErrorTrackingMapper {

    public List<ErrorTrackingModel> mapErrorModels(Collection<ErrorTrackingEntity> errors) {
        return errors.stream().map(this::mapErrorModel).collect(Collectors.toList());
    }

    public ErrorTrackingModel mapErrorModel(ErrorTrackingEntity error) {
        ErrorTrackingModel model = new ErrorTrackingModel();
        model.setId(error.getId());
        model.setMessage(error.getMessage());
        model.setDate(error.getDate());
        model.setReferenceId(error.getReferenceId());
        model.setReferenceType(error.getReferenceType());

        return model;
    }
}
