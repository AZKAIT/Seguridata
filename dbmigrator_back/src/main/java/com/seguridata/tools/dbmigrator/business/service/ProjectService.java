package com.seguridata.tools.dbmigrator.business.service;

import com.seguridata.tools.dbmigrator.business.client.StompMessageClient;
import com.seguridata.tools.dbmigrator.business.exception.EmptyResultException;
import com.seguridata.tools.dbmigrator.business.exception.InvalidUpdateException;
import com.seguridata.tools.dbmigrator.business.exception.MissingObjectException;
import com.seguridata.tools.dbmigrator.business.exception.ObjectLockedException;
import com.seguridata.tools.dbmigrator.data.constant.ProjectStatus;
import com.seguridata.tools.dbmigrator.data.entity.ConnectionEntity;
import com.seguridata.tools.dbmigrator.data.entity.ProjectEntity;
import com.seguridata.tools.dbmigrator.data.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.seguridata.tools.dbmigrator.data.constant.ProjectStatus.CREATED;
import static com.seguridata.tools.dbmigrator.data.constant.ProjectStatus.RUNNING;
import static com.seguridata.tools.dbmigrator.data.constant.ProjectStatus.STARTING;
import static com.seguridata.tools.dbmigrator.data.constant.ProjectStatus.STOPPED;
import static com.seguridata.tools.dbmigrator.data.constant.ProjectStatus.STOPPING;

@Service
public class ProjectService {

    private final ProjectRepository projectRepo;
    private final StompMessageClient stompMsgClient;

    @Autowired
    public ProjectService(ProjectRepository projectRepo,
                          StompMessageClient stompMsgClient) {
        this.projectRepo = projectRepo;
        this.stompMsgClient = stompMsgClient;
    }

    public ProjectEntity createProject(ProjectEntity project) {
        project.setId(null);
        project.setStatus(CREATED);
        project.setCreatedAt(new Date());
        project.setLastStatusDate(new Date());

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
        updatedProject.setStatus(existingProject.getStatus());
        updatedProject.setLastStatusDate(existingProject.getLastStatusDate());

        return this.projectRepo.updateProject(updatedProject);
    }

    public ProjectEntity deleteProject(ProjectEntity project) {
        return this.projectRepo.deleteProject(project.getId());
    }

    public void validateProjectStatus(ProjectEntity project) {
        if (Objects.isNull(project)) {
            throw new MissingObjectException("No se encontró el Proyecto");
        }

        if (!CREATED.equals(project.getStatus()) && !STOPPED.equals(project.getStatus())) {
            throw new ObjectLockedException("El Proyecto debe estar en estatus CREATED o STOPPED para actualizar los valores");
        }
    }

    public boolean updateProjectStatus(ProjectEntity project, ProjectStatus newStatus) {
        if (CREATED.equals(newStatus)) {
            throw new InvalidUpdateException("No se puede regresar el estatus del Proyecto a CREATED");
        }

        ProjectStatus currentStatus = project.getStatus();
        if (CREATED.equals(currentStatus) && !STARTING.equals(newStatus)) {
            throw new InvalidUpdateException("Proyectos en estatus CREATED solo pueden transicionar a estatus STARTING");
        }

        if (STARTING.equals(currentStatus) && (!RUNNING.equals(newStatus) && !STOPPED.equals(newStatus))) {
            throw new InvalidUpdateException("Proyectos en estatus STARTING solo pueden transicionar a estatus RUNNING o STOPPED");
        }

        if (STOPPING.equals(currentStatus) && !STOPPED.equals(newStatus)) {
            throw new InvalidUpdateException("Proyectos en estatus STOPPING solo pueden transicionar a estatus STOPPED");
        }

        project.setStatus(newStatus);
        boolean updated = this.projectRepo.updateProjectStatus(project.getId(), newStatus);
        if (updated) {
            this.stompMsgClient.sendProjectStatusChange(project, newStatus);
        }
        return updated;
    }

    public void projectContainsConn(ConnectionEntity connection) {
        if (this.projectRepo.projectContainsConn(connection.getId())) {
            throw new ObjectLockedException("La Conexión está asociada a un Proyecto, no se puede eliminar");
        }
    }
}
