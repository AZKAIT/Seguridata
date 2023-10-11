package com.seguridata.tools.dbmigrator.business.service;

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

    @Autowired
    public ProjectService(ProjectRepository projectRepo) {
        this.projectRepo = projectRepo;
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
            throw new MissingObjectException("Project doesn't exist");
        }

        return project;
    }

    public List<ProjectEntity> getAllProjects() {
        List<ProjectEntity> projects = this.projectRepo.getAllProjects();

        if (CollectionUtils.isEmpty(projects)) {
            throw new EmptyResultException("No Projects found");
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
            throw new MissingObjectException("Couldn't find Project");
        }

        if (!CREATED.equals(project.getStatus()) && !STOPPED.equals(project.getStatus())) {
            throw new ObjectLockedException("Project should be in CREATED or STOPPED state to update values");
        }
    }

    public boolean updateProjectStatus(ProjectEntity project, ProjectStatus newStatus) {
        if (CREATED.equals(newStatus)) {
            throw new InvalidUpdateException("Can't update Project status to CREATED");
        }

        ProjectStatus currentStatus = project.getStatus();
        if (CREATED.equals(currentStatus) && !STARTING.equals(newStatus)) {
            throw new InvalidUpdateException("CREATED Project status can only transition to STARTING");
        }

        if (STARTING.equals(currentStatus) && (!RUNNING.equals(newStatus) && !STOPPED.equals(newStatus))) {
            throw new InvalidUpdateException("STARTING Project status can only transition to RUNNING or STOPPED");
        }

        if (STOPPING.equals(currentStatus) && !STOPPED.equals(newStatus)) {
            throw new InvalidUpdateException("STOPPING Project status can only transition to STOPPED");
        }

        project.setStatus(newStatus);
        return this.projectRepo.updateProjectStatus(project.getId(), newStatus);
    }

    public void projectContainsConn(ConnectionEntity connection) {
        if (this.projectRepo.projectContainsConn(connection.getId())) {
            throw new ObjectLockedException("Connection is present in Project, can't delete");
        }
    }
}
