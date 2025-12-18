package org.lab.services.impl;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.lab.entities.Action;
import org.lab.entities.EntityType;
import org.lab.entities.Role;
import org.lab.services.AccessControlService;
import org.lab.services.ProjectService;

import static org.lab.entities.Action.CREATE;
import static org.lab.entities.Action.READ;
import static org.lab.entities.Action.UPDATE;
import static org.lab.entities.Action.UPDATE_STATUS;

import static org.lab.entities.EntityType.MILESTONE;
import static org.lab.entities.EntityType.TICKET;
import static org.lab.entities.EntityType.BUG_REPORT;

public class AccessControlServiceImpl implements AccessControlService {
    private static final Map<Role, Map<EntityType, Set<Action>>> roleModel = Map.of(
            Role.Teamlead, Map.of(
                    MILESTONE, Set.of(READ),
                    BUG_REPORT, Set.of(CREATE, READ, UPDATE_STATUS),
                    TICKET, Set.of(CREATE, READ, UPDATE, UPDATE_STATUS)),
            Role.Manager, Map.of(
                    MILESTONE, Set.of(CREATE, READ, UPDATE, UPDATE_STATUS),
                    BUG_REPORT, Set.of(READ),
                    TICKET, Set.of(CREATE, READ, UPDATE, UPDATE_STATUS)),
            Role.Developer, Map.of(
                    MILESTONE, Set.of(READ),
                    TICKET, Set.of(READ, UPDATE_STATUS),
                    BUG_REPORT, Set.of(CREATE, READ, UPDATE_STATUS)),
            Role.QA, Map.of(
                    MILESTONE, Set.of(READ),
                    TICKET, Set.of(READ),
                    BUG_REPORT, Set.of(CREATE, READ, UPDATE_STATUS)));

    private final ProjectService projectService;

    public AccessControlServiceImpl(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Override
    public boolean hasAccess(UUID projectId, UUID userId, EntityType entity, Action action) {
        return projectService.listUsersByProject(projectId).stream()
                .filter(user -> user.userId().equals(userId))
                .findFirst()
                .map(user -> hasAccess(entity, action, user.role()))
                .orElse(false);
    }

    public boolean hasAccess(EntityType entity, Action action, Role role) {
        return Optional.ofNullable(roleModel.get(role))
                .map(actionsByEntity -> actionsByEntity.get(entity))
                .map(set -> set.contains(action))
                .orElse(false);
    }
}
