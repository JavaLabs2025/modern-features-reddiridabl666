package org.lab.services;

import java.util.List;
import java.util.UUID;

import org.lab.entities.Project;
import org.lab.entities.ProjectUser;
import org.lab.entities.Role;
import org.lab.entities.User;

public interface ProjectService {
    Project create(User user, String name);

    void addUser(User requester, UUID projectId, UUID userId, Role role);

    List<ProjectUser> listUsersByProject(UUID projectId);

    List<ProjectUser> listProjectsByUser(UUID userId);
}
