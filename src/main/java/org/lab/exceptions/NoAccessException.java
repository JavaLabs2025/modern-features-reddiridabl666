package org.lab.exceptions;

import java.util.UUID;

import org.lab.entities.Action;
import org.lab.entities.EntityType;

public class NoAccessException extends RuntimeException {
    public NoAccessException(EntityType entityType, Action action, UUID projectId) {
        super(String.format("User has no access to %s %s in project %s", action, entityType, projectId));
    }
}
