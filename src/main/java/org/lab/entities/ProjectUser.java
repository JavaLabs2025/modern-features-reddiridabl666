package org.lab.entities;

import java.util.UUID;

public record ProjectUser(UUID projectId, UUID userId, Role role) {
}
