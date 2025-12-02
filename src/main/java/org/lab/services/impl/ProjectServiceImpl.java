package org.lab.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.lab.entities.Project;
import org.lab.entities.ProjectUser;
import org.lab.services.ProjectService;

public class ProjectServiceImpl implements ProjectService {
    private final Map<UUID, Project> projects = new HashMap<>();

    @Override
    public void create(Project project) {
        projects.put(project.getId(), project);
    }

    @Override
    public void addUser(UUID projectId, ProjectUser user) {
        var project = projects.get(projectId);
        if (project == null) {
            throw new RuntimeException("not found");
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
