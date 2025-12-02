package org.lab.entities;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectUser {
    private final UUID projectId;
    private final UUID userId;
    private final Role role;
}
