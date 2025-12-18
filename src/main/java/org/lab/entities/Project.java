package org.lab.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Getter;

@Getter
public class Project {
    private final UUID id;

    private final UUID createdBy;

    private final String name;

    private final List<ProjectUser> users = new ArrayList<>();

    public Project(UUID id, UUID createdBy, String name) {
        this.id = id;
        this.createdBy = createdBy;
        this.name = name;
    }

    public void addUser(ProjectUser user) {
        users.add(user);
    }
}
