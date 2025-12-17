package org.lab.services.impl;

import static org.lab.entities.Action.UPDATE;
import static org.lab.entities.EntityType.PROJECT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.lab.entities.Project;
import org.lab.entities.ProjectUser;
import org.lab.entities.Role;
import org.lab.entities.User;
import org.lab.exceptions.NoAccessException;
import org.lab.services.ProjectService;

public class ProjectServiceImpl implements ProjectService {
    private final Map<UUID, Project> projects = new HashMap<>();

    @Override
    public Project create(User user) {
        var projectId = UUID.randomUUID();

        var project = new Project(projectId, user.getId());
        projects.put(projectId, project);

        return project;
    }

    @Override
    public void addUser(User requester, UUID projectId, ProjectUser user) {
        if (user.getRole() == Role.Manager) {
            throw new IllegalArgumentException("Manager role is not supported");
        }

        var project = projects.get(projectId);
        if (project == null) {
            throw new RuntimeException("not found");
        }

        var teamleadsCount = project.getUsers().stream()
                .filter(projectUser -> projectUser.getRole() == Role.Teamlead)
                .count();

        if (user.getRole() == Role.Teamlead && teamleadsCount > 0) {
            throw new IllegalArgumentException("Only one teamlead allowed for project");
        }

        if (!project.getCreatedBy().equals(requester.getId())) {
            throw new NoAccessException(PROJECT, UPDATE, projectId);
        }

        project.addUser(user);
    }

    @Override
    public List<ProjectUser> listUsersByProject(UUID projectId) {
        var project = projects.get(projectId);
        if (project == null) {
            throw new RuntimeException("not found");
        }

        return project.getUsers();
    }

    @Override
    public List<ProjectUser> listProjectsByUser(UUID userId) {
        var result = new ArrayList<ProjectUser>();

        projects.values().stream()
                .flatMap(project -> project.getUsers().stream())
                .filter(user -> user.getUserId().equals(userId))
                .findFirst()
                .ifPresent(user -> result.add(user));

        return result;
    }
}
