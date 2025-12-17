package org.lab.exceptions;

import org.lab.entities.EntityType;

public class IllegalStatusException extends RuntimeException {
    public IllegalStatusException(EntityType entityType, Object currentStatus, Object allowedStatus) {
        super(String.format("Only %s status can be set for a %s in %s status", allowedStatus, entityType,
                currentStatus));
    }
}
