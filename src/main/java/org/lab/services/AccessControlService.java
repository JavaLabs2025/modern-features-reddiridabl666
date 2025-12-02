package org.lab.services;

import org.lab.entities.Action;
import org.lab.entities.EntityType;
import org.lab.entities.Role;

public interface AccessControlService {
    boolean hasAccess(EntityType entity, Action action, Role role);
}
