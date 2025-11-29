package org.lab.services;

import java.util.List;
import java.util.UUID;

import org.lab.entities.Project;
import org.lab.entities.ProjectUser;

public interface ProjectService {
    void create(Project project);

    void addUser(UUID projectId, ProjectUser user);

    List<ProjectUser> listUsersByProject(UUID projectId);

    List<ProjectUser> listProjectsByUser(UUID userId);
}
