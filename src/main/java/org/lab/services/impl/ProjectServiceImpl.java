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
import org.lab.exceptions.NotFoundException;
import org.lab.services.ProjectService;
import org.lab.services.UserService;

public class ProjectServiceImpl implements ProjectService {
    private final Map<UUID, Project> projects = new HashMap<>();

    private final UserService userService;

    public ProjectServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Project create(User user, String name) {
        var projectId = UUID.randomUUID();

        var project = new Project(projectId, user.id(), name);
        project.addUser(new ProjectUser(projectId, user.id(), Role.Manager));

        projects.put(projectId, project);

        return project;
    }

    @Override
    public void addUser(User requester, UUID projectId, UUID userId, Role role) {
        if (role == Role.Manager) {
            throw new IllegalArgumentException("Manager role is not supported");
        }

        var project = projects.get(projectId);
        if (project == null) {
            throw new NotFoundException();
        }

        boolean userExists = userService.listUsers().stream()
                .filter(user -> user.id().equals(userId))
                .findFirst()
                .isPresent();

        if (!userExists) {
            throw new NotFoundException();
        }

        if (!project.getCreatedBy().equals(requester.id())) {
            throw new NoAccessException(PROJECT, UPDATE, projectId);
        }

        var teamleadsCount = project.getUsers().stream()
                .filter(projectUser -> projectUser.role() == Role.Teamlead)
                .count();

        if (role == Role.Teamlead && teamleadsCount > 0) {
            throw new IllegalArgumentException("Only one teamlead allowed for project");
        }

        project.addUser(new ProjectUser(projectId, userId, role));
    }

    @Override
    public List<ProjectUser> listUsersByProject(UUID projectId) {
        var project = projects.get(projectId);
        if (project == null) {
            throw new NotFoundException();
        }

        return project.getUsers();
    }

    @Override
    public List<ProjectUser> listProjectsByUser(UUID userId) {
        var result = new ArrayList<ProjectUser>();

        projects.values().stream()
                .flatMap(project -> project.getUsers().stream())
                .filter(user -> user.userId().equals(userId))
                .findFirst()
                .ifPresent(user -> result.add(user));

        return result;
    }
}
