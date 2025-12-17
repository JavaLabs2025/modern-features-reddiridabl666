package org.lab.services;

import java.util.UUID;

import org.lab.entities.Action;
import org.lab.entities.EntityType;

public interface AccessControlService {
    boolean hasAccess(UUID projectId, UUID userId, EntityType entity, Action action);
}
