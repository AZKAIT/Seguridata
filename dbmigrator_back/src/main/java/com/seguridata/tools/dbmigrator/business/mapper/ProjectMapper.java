package com.seguridata.tools.dbmigrator.business.mapper;

import com.seguridata.tools.dbmigrator.data.entity.ProjectEntity;
import com.seguridata.tools.dbmigrator.data.model.ProjectModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ProjectMapper {

    private final ConnectionMapper connectionMapper;

    @Autowired
    public ProjectMapper(ConnectionMapper connectionMapper) {
        this.connectionMapper = connectionMapper;
    }

    public List<ProjectModel> mapProjectModelList(Collection<ProjectEntity> projects) {
        return projects.stream().filter(Objects::nonNull).map(this::mapProjectModel).collect(Collectors.toList());
    }

    public ProjectEntity mapProjectEntity(ProjectModel projectModel) {
        ProjectEntity project = new ProjectEntity();
        project.setId(projectModel.getId());
        project.setName(projectModel.getName());
        project.setDescription(projectModel.getDescription());
        project.setCreatedAt(projectModel.getCreatedAt());
        project.setParallelThreads(projectModel.getParallelThreads());
        project.setLocked(projectModel.getLocked());

        project.setSourceConnection(this.connectionMapper.mapConnectionEntity(projectModel.getSourceConnection()));
        project.setTargetConnection(this.connectionMapper.mapConnectionEntity(projectModel.getTargetConnection()));

        return project;
    }

    public ProjectModel mapProjectModel(ProjectEntity project) {
        ProjectModel projectModel = new ProjectModel();
        projectModel.setId(project.getId());
        projectModel.setName(project.getName());
        projectModel.setDescription(project.getDescription());
        projectModel.setCreatedAt(project.getCreatedAt());
        projectModel.setParallelThreads(project.getParallelThreads());
        projectModel.setLocked(project.getLocked());

        projectModel.setSourceConnection(this.connectionMapper.mapConnectionModel(project.getSourceConnection()));
        projectModel.setTargetConnection(this.connectionMapper.mapConnectionModel(project.getTargetConnection()));

        return projectModel;
    }
}
