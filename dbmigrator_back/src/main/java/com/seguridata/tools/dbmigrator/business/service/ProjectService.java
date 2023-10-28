package com.seguridata.tools.dbmigrator.business.service;

import com.seguridata.tools.dbmigrator.business.exception.EmptyResultException;
import com.seguridata.tools.dbmigrator.business.exception.MissingObjectException;
import com.seguridata.tools.dbmigrator.business.exception.ObjectLockedException;
import com.seguridata.tools.dbmigrator.data.entity.ConnectionEntity;
import com.seguridata.tools.dbmigrator.data.entity.ProjectEntity;
import com.seguridata.tools.dbmigrator.data.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import static java.lang.Boolean.TRUE;

@Service
public class ProjectService {

    private final ProjectRepository projectRepo;

    @Autowired
    public ProjectService(ProjectRepository projectRepo) {
        this.projectRepo = projectRepo;
    }

    public ProjectEntity createProject(ProjectEntity project) {
        project.setId(null);
        project.setCreatedAt(new Date());
        project.setLocked(false);

        return this.projectRepo.createProject(project);
    }

    public ProjectEntity getProject(String projectId) {
        ProjectEntity project = this.projectRepo.getProject(projectId);

        if (Objects.isNull(project)) {
            throw new MissingObjectException("El Proyecto no existe");
        }

        return project;
    }

    public List<ProjectEntity> getAllProjects() {
        List<ProjectEntity> projects = this.projectRepo.getAllProjects();

        if (CollectionUtils.isEmpty(projects)) {
            throw new EmptyResultException("No se encontraron Proyectos");
        }

        return projects;
    }

    public ProjectEntity updateProject(ProjectEntity existingProject, ProjectEntity updatedProject) {
        updatedProject.setId(existingProject.getId());
        updatedProject.setCreatedAt(existingProject.getCreatedAt());
        updatedProject.setLocked(existingProject.getLocked());

        return this.projectRepo.updateProject(updatedProject);
    }

    public ProjectEntity deleteProject(ProjectEntity project) {
        return this.projectRepo.deleteProject(project.getId());
    }

    public void validateProjectStatus(ProjectEntity project) {
        if (Objects.isNull(project)) {
            throw new MissingObjectException("No se encontró el Proyecto");
        }

        if (TRUE.equals(project.getLocked())) {
            throw new ObjectLockedException("El Proyecto está bloqueado, no es posible actualizar los valores");
        }
    }

    public void updateProjectLocked(ProjectEntity project, boolean locked) {
        project.setLocked(locked);
        this.projectRepo.updateProject(project);
    }

    public void projectContainsConn(ConnectionEntity connection) {
        if (this.projectRepo.projectContainsConn(connection.getId())) {
            throw new ObjectLockedException("La Conexión está asociada a un Proyecto, no se puede eliminar");
        }
    }
}
