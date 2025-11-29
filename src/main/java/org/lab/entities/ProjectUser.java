package org.lab.entities;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectUser {
    private final UUID id;
    private final Role role;
}
